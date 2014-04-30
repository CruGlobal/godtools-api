package org.cru.godtools.migration;

import com.google.common.base.Strings;
import org.cru.godtools.api.languages.LanguageService;
import org.cru.godtools.api.packages.domain.*;
import org.cru.godtools.api.packages.utils.LanguageCode;
import org.cru.godtools.api.packages.utils.XmlDocumentSearchUtilities;
import org.cru.godtools.api.translations.domain.Translation;
import org.cru.godtools.api.translations.domain.TranslationService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/30/14.
 */
public class TranslatableElements
{
	private String packageCode;
	private String languageCode;
	private Document targetTranslationXmlContent;
	private Document baseTranslationXmlContent;

	public TranslatableElements(String packageCode, String languageCode, Document targetTranslationXmlContent, Document baseTranslationXmlContent)
	{
		this.packageCode = packageCode;
		this.languageCode = languageCode;
		this.targetTranslationXmlContent = targetTranslationXmlContent;
		this.baseTranslationXmlContent = baseTranslationXmlContent;
	}

	public void save(TranslationService translationService, LanguageService languageService, PackageService packageService, TranslationElementService translationElementService)
	{

		Translation englishTranslation = translationService.selectByLanguageIdPackageId(languageService.selectByLanguageCode(new LanguageCode("en")).getId(),
				packageService.selectByCode(packageCode).getId());

		Translation targetTranslation = translationService.selectByLanguageIdPackageId(languageService.selectByLanguageCode(new LanguageCode(languageCode)).getId(),
				packageService.selectByCode(packageCode).getId());

		List<Element> targetTranslationElements = XmlDocumentSearchUtilities.findElementsWithAttribute(targetTranslationXmlContent, "translate");
		List<Element> baseTranslationElements = XmlDocumentSearchUtilities.findElementsWithAttribute(baseTranslationXmlContent, "translate");

		if(targetTranslationElements.size() != baseTranslationElements.size())
		{
			//argh
		}

		for(int i = 0; i < baseTranslationElements.size() && i < targetTranslationElements.size(); i++)
		{
			Element baseXmlElement = baseTranslationElements.get(i);
			Element targetXmlElement = targetTranslationElements.get(i);

			if(Boolean.parseBoolean(targetXmlElement.getAttribute("translate")))
			{
				UUID translationElementId = !Strings.isNullOrEmpty(baseXmlElement.getAttribute("gtapi-trx-id")) ? UUID.fromString(baseXmlElement.getAttribute("gtapi-trx-id")) : UUID.randomUUID();
				targetXmlElement.setAttribute("gtapi-trx-id", translationElementId.toString());

				TranslationElement translationElement = new TranslationElement();
				translationElement.setId(translationElementId);
				translationElement.setTranslationId(targetTranslation.getId());
				translationElement.setBaseText(baseXmlElement.getTextContent());
				translationElement.setTranslatedText(targetXmlElement.getTextContent());
				translationElement.setElementType(targetXmlElement.getNodeName());
				translationElement.setDisplayOrder(i);

				translationElementService.insert(translationElement);
			}
		}
	}
}
