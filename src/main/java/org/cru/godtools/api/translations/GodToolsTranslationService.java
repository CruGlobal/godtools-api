package org.cru.godtools.api.translations;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.spy.memcached.MemcachedClient;
import org.cru.godtools.api.cache.GodToolsCache;
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
	@Inject
	private GodToolsCache cache;

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

		updateCache(translation, pageStructure);

		return pageStructure;
	}

	private void updateCache(Translation translation, PageStructure pageStructure)
	{
		Optional<GodToolsTranslation> possibleTranslation = cache.get(translation.getId());
		if(possibleTranslation.isPresent())
		{
			GodToolsTranslation godToolsTranslation = possibleTranslation.get();

			godToolsTranslation.replacePageXml(pageStructure);
			logger.info(String.format("replacing page %s in cached translation %s", pageStructure.getId(), translation.getId()));
			cache.replace(godToolsTranslation);
		}
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

		Optional<GodToolsTranslation> possibleTranslation = cache.get(translation.getId());
		if(possibleTranslation.isPresent())
		{
			logger.info(String.format("found translation %s in cache", translation.getId()));
			return possibleTranslation.get();
		}

		Package gtPackage = packageService.selectByCode(packageCode);
		PackageStructure packageStructure = packageStructureService.selectByPackageId(gtPackage.getId());
		List<PageStructure> pageStructures = pageStructureService.selectByTranslationId(translation.getId());

		List<TranslationElement> translationElementList = translationElementService.selectByTranslationId(translation.getId());

		GodToolsTranslation godToolsTranslation = GodToolsTranslation.assembleFromComponents(packageCode,
				translation,
				packageStructure,
				pageStructures,
				translationElementList,
				getImagesUsedInThisTranslation(packageStructure),
				!translation.isReleased(),
				loadIcon(packageCode));

		logger.info(String.format("adding translation %s to cache", translation.getId()));
		cache.add(godToolsTranslation);

		return godToolsTranslation;
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
	public Translation setupNewTranslation(LanguageCode languageCode, String packageCode)
	{
		Package gtPackage = packageService.selectByCode(packageCode);
		Language language = languageService.getOrCreateLanguage(languageCode);

		// try to load out the latest version of translation for this package/language combo
		Translation currentTranslation = getTranslationFromDatabase(new LanguageCode(language.getCode()), gtPackage.getCode(), GodToolsVersion.LATEST_VERSION);

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
			newTranslationProcess.copyPageAndTranslationData(currentTranslation, newTranslation);
			newTranslationProcess.copyPackageTranslationData(currentTranslation, newTranslation);
		}
		else
		{
			Translation baseTranslation = loadBaseTranslation(gtPackage);
			newTranslationProcess.copyPageAndTranslationData(baseTranslation, newTranslation);
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
		Translation translation = getTranslationFromDatabase(languageCode, packageCode, GodToolsVersion.LATEST_VERSION);
		translation.setReleased(true);
		translationService.update(translation);

		// remove the translation from the cache b/c it will have draft status.  it will be replaced with the new "live" version
		cache.remove(translation.getId());
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
