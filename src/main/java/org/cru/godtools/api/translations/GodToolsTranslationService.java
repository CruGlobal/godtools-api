package org.cru.godtools.api.translations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.IOException;
import javax.xml.transform.TransformerException;
import org.cru.godtools.api.translations.model.ConfigFile;
import org.cru.godtools.domain.GodToolsVersion;
import org.cru.godtools.domain.images.Image;
import org.cru.godtools.domain.images.ImageService;
import org.cru.godtools.domain.images.ReferencedImage;
import org.cru.godtools.domain.images.ReferencedImageService;
import org.cru.godtools.domain.languages.Language;
import org.cru.godtools.domain.languages.LanguageCode;
import org.cru.godtools.domain.languages.LanguageService;
import org.cru.godtools.domain.packages.Package;
import org.cru.godtools.domain.packages.PackageService;
import org.cru.godtools.domain.packages.PackageStructure;
import org.cru.godtools.domain.packages.PackageStructureService;
import org.cru.godtools.domain.packages.PageStructure;
import org.cru.godtools.domain.packages.PageStructureService;
import org.cru.godtools.domain.packages.TranslationElement;
import org.cru.godtools.domain.packages.TranslationElementService;
import org.cru.godtools.domain.translations.Translation;
import org.cru.godtools.domain.translations.TranslationService;
import org.jboss.logging.Logger;
import org.w3c.dom.Document;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service which uses lower-level domain services to assemble XML structure files for a translation of a GodTools translation
 * and return the results bundled as a GodToolsTranslation.
 *
 * There are services for a specific language & package combo, as well as a service which would load all packages
 * for a language and return them as a Set.
 *
 * Created by ryancarlson on 4/8/14.
 */
@Deprecated
public class GodToolsTranslationService
{
	@Inject
	protected PackageService packageService;
	@Inject
	protected TranslationService translationService;
	@Inject
	protected LanguageService languageService;
	@Inject
	protected PackageStructureService packageStructureService;
	@Inject
	protected PageStructureService pageStructureService;
	@Inject
	protected TranslationElementService translationElementService;
	@Inject
	protected ReferencedImageService referencedImageService;
	@Inject
	protected ImageService imageService;
	@Inject
	private NewTranslationCreation newTranslationProcess;
	@Inject
	private DraftTranslation draftTranslationProcess;

	private Logger logger = Logger.getLogger(GodToolsTranslationService.class);

	/**
	 * Retrieves a specific page of a translation
	 */
	public PageStructure getPage(LanguageCode languageCode, UUID pageId)
	{
		PageStructure pageStructure = pageStructureService.selectByid(pageId);
		Translation translation = translationService.selectById(pageStructure.getTranslationId());
		Package gtPackage = packageService.selectById(translation.getPackageId());
		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());

		draftTranslationProcess.updateFromTranslationTool(gtPackage.getTranslationProjectId(),
				translation,
				Lists.newArrayList(pageStructure),
				languageCode);

		List<TranslationElement> translationElementList = translationElementService.selectByTranslationIdPageStructureId(translation.getId(),
				pageId);

		pageStructure.setTranslatedFields(TranslationElement.createMapOfTranslationElements(translationElementList));
		pageStructure.replaceImageNamesWithImageHashes(Image.createMapOfImages(getImagesUsedInThisTranslation(packageStructure)));

		return pageStructure;
	}

	public void updatePageLayout(UUID packageId, String filename, Document updatedPageLayout)
	{
		for(PageStructure pageStructure : loadPageStructures(packageId, filename))
		{
			pageStructure.mergeXmlContent(updatedPageLayout);

			pageStructureService.update(pageStructure);
		}
	}

	public void addToPageLayout(UUID packageId, String filename, Document updatedPageLayout) throws IOException, TransformerException
	{
		for(PageStructure pageStructure : loadPageStructures(packageId, filename))
		{
			pageStructure.addXmlContent(updatedPageLayout);

			pageStructureService.update(pageStructure);
		}
	}

	public void removeFromPageLayout(UUID packageId, String filename, Document updatedPageLayout)
	{
		for(PageStructure pageStructure : loadPageStructures(packageId, filename))
		{
			pageStructure.removeXmlContent(updatedPageLayout);

			pageStructureService.update(pageStructure);
		}
	}

	public void updatePageLayout(UUID pageId, Document updatedPageLayout)
	{
		PageStructure pageStructure = pageStructureService.selectByid(pageId);
		pageStructure.mergeXmlContent(updatedPageLayout);

		pageStructureService.update(pageStructure);
	}

	public void addToPageLayout(UUID pageId, Document updatedPageLayout) throws IOException, TransformerException
	{
		PageStructure pageStructure = pageStructureService.selectByid(pageId);
		pageStructure.addXmlContent(updatedPageLayout);

		pageStructureService.update(pageStructure);
	}

	public void removeFromPageLayout(UUID pageId, Document updatedPageLayout)
	{
		PageStructure pageStructure = pageStructureService.selectByid(pageId);
		pageStructure.removeXmlContent(updatedPageLayout);

		pageStructureService.update(pageStructure);
	}

	private List<PageStructure> loadPageStructures(UUID packageId, String filename)
	{
		List<PageStructure> pageStructureList = Lists.newArrayList();

		for(Language language : languageService.selectAllLanguages())
		{
			logger.info(String.format("Loading latest translation for %s", language.getName()));
			Translation translation = translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(), packageId, GodToolsVersion.LATEST_VERSION);

			// we won't create any brand new translations at this point.
			if(translation == null) continue;

			if(translation.isReleased())
			{
				logger.info(String.format("Creating new draft translation for %s", language.getName()));
				translation = setupNewTranslation(language.getId(), packageId);
			}

			PageStructure pageStructure = pageStructureService.selectByTranslationIdAndFilename(translation.getId(), filename);
			if(pageStructure != null)
			{
				logger.info(String.format("Loaded page structure w/ ID %S for Language %s", pageStructure.getId(), language.getName()));
				pageStructureList.add(pageStructure);
			}
			else
			{
				logger.info(String.format("No page structure for Language %s", language.getName()));
			}
		}

		return pageStructureList;
	}

	public ConfigFile getConfig(String packageCode, LanguageCode languageCode, GodToolsVersion version)
	{
		Package gtPackage = packageService.selectByCode(packageCode);
		Translation translation = getTranslationFromDatabase(languageCode, packageCode, version);
		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());

		packageStructure.replacePageNamesWithPageHashes(
				PageStructure.createMapOfPageStructures(
						pageStructureService.selectByTranslationId(translation.getId())
				)
		);

		packageStructure.setTranslatedFields(
				TranslationElement.createMapOfTranslationElements(
						translationElementService.selectByTranslationId(translation.getId())
				)
		);

		return ConfigFile.createConfigFile(packageStructure);
	}

	public GodToolsTranslation getTranslation(Translation translation)
	{
		Package gtPackage = packageService.selectById(translation.getPackageId());
		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());
		List<PageStructure> pageStructures = pageStructureService.selectByTranslationId(translation.getId());
		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(translation.getId());

		GodToolsTranslation godToolsTranslation = GodToolsTranslation.assembleFromComponents(gtPackage,
				languageService.selectLanguageById(translation.getLanguageId()),
				translation,
				packageStructure,
				pageStructures,
				translationElementList,
				getImagesUsedInThisTranslation(packageStructure),
				loadIcon(gtPackage.getCode()));

		logger.info(String.format("adding translation %s to cache", translation.getId()));

		return godToolsTranslation;
	}

	/**
	 * Retrieves a specific package in a specific language at a specific version.
	 */
	public GodToolsTranslation getTranslation(LanguageCode languageCode,
											  String packageCode,
											  GodToolsVersion godToolsVersion)
	{
		Translation translation = getTranslationFromDatabase(languageCode, packageCode, godToolsVersion);

		if(translation == null) throw new NotFoundException();

		return getTranslation(translation);
	}

	/**
	 * Retrieves the latest published version of all packages for specified language.
	 *
	 * @return
	 */
	public Set<GodToolsTranslation> getTranslationsForLanguage(LanguageCode languageCode, GodToolsVersion godToolsVersion)
	{
		Set<GodToolsTranslation> translations = Sets.newHashSet();

		for(Package gtPackage : packageService.selectAllPackages())
		{
			try
			{
				translations.add(getTranslation(languageCode, gtPackage.getCode(), godToolsVersion));
			}
			catch(NotFoundException notFound) { /*oh well..*/ }
		}

		return translations;
	}

	/**
	 * Creates a new translations for the languageCode and packageCode combination that's specified.
	 *
	 * If the languageCode doesn't reference a valid language, a new language is created.
	 *
	 * The newly created translation is version 1 for a brand new translation, or if it's a new version of an existing
	 * translation, then it takes the next highest version number.
	 */
	public Translation setupNewTranslation(UUID languageId, UUID packageId)
	{
		Package gtPackage = packageService.selectById(packageId);
		Language language = languageService.selectLanguageById(languageId);

		// try to load out the latest version of translation for this package/language combo
		Translation currentTranslation = getTranslationFromDatabase(LanguageCode.fromLanguage(language), gtPackage.getCode(), GodToolsVersion.LATEST_VERSION);

		// only allow one draft per translation
		if(currentTranslation != null && currentTranslation.isDraft()) return currentTranslation;

		// save a new translation for this package language combo
		Translation newTranslation = newTranslationProcess.saveNewTranslation(gtPackage, language, currentTranslation);
		logger.info(String.format("created translation %s", newTranslation.getId()));

		// if we found a current translation, then copy page structures and translation elements from the current translation
		// to the new
		logger.info("Starting translation data copy at");
		if (currentTranslation != null)
		{
			newTranslationProcess.copyPageAndTranslationData(currentTranslation, newTranslation, false);
			newTranslationProcess.copyPackageTranslationData(currentTranslation, newTranslation);
		}
		else
		{
			Translation baseTranslation = loadBaseTranslation(gtPackage);
			newTranslationProcess.copyPageAndTranslationData(baseTranslation, newTranslation, true);
			newTranslationProcess.copyPackageTranslationData(baseTranslation, newTranslation);
		}
		logger.info("Finished translation data copy");

		logger.info("Starting async upload");
		newTranslationProcess.uploadToTranslationTool(gtPackage, language);
		logger.info("Finished async upload");

		return newTranslation;
	}

	public void publishDraftTranslation(LanguageCode languageCode, String packageCode)
	{
		publishDraftTranslation(getTranslationFromDatabase(languageCode, packageCode, GodToolsVersion.LATEST_VERSION));
	}

	public void publishDraftTranslation(Translation translation)
	{
		translation.setReleased(true);
		translationService.update(translation);
	}

	private List<Image> getImagesUsedInThisTranslation(PackageStructure packageStructure)
	{
		List<ReferencedImage> referencedImages = referencedImageService.selectByPackageStructureId(packageStructure.getId(), true);

		List<Image> imageList = Lists.newArrayList();

		for(ReferencedImage referencedImage : referencedImages)
		{
			imageList.add(imageService.selectById(referencedImage.getImageId()));
		}

		return imageList;
	}

	private Translation loadBaseTranslation(Package gtPackage)
	{
		return translationService.selectByLanguageIdPackageIdVersionNumber(languageService.selectByLanguageCode(new LanguageCode("en")).getId(),
				gtPackage.getId(),
				GodToolsVersion.LATEST_PUBLISHED_VERSION);
	}

	private Translation getTranslationFromDatabase(LanguageCode languageCode, String packageCode, GodToolsVersion godToolsVersion)
	{
		Language language = languageService.selectByLanguageCode(languageCode);
		Package gtPackage = packageService.selectByCode(packageCode);

		return translationService.selectByLanguageIdPackageIdVersionNumber(language.getId(), gtPackage.getId(), godToolsVersion);
	}

	private Image loadIcon(String packageCode)
	{
		return imageService.selectByFilename(Image.buildFilename(packageCode, "icon@2x.png"));
	}
}
