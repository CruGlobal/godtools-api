package org.cru.godtools.api.packages.domain;

import org.cru.godtools.api.packages.utils.ShaGenerator;
import org.testng.Assert;
import org.w3c.dom.Document;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class PageServiceTestMockDataService
{
	public void persistPage(PageService pageService)
	{
		Page page = new Page();
		page.setId(PageServiceTest.TEST_PAGE_ID);
		page.setVersionId(PageServiceTest.TEST_VERSION_ID);
		page.setDescription("Test Page");
		page.setXmlContent(null);

		pageService.insert(page);
	}

	public void modifyPage(PageService pageService)
	{
		Page page = new Page();
		page.setId(PageServiceTest.TEST_PAGE_ID);
		page.setVersionId(PageServiceTest.TEST_VERSION_ID);
		page.setDescription("Updated page");
		page.setXmlContent(null);

		pageService.update(page);
	}

	public void persistVersion(VersionService versionService)
	{
		Version version = new Version();
		version.setId(PageServiceTest.TEST_VERSION_ID);
		version.setVersionNumber(1);

		versionService.insert(version);
	}

	public void validatePage(Page page)
	{
		Assert.assertNotNull(page);
		Assert.assertEquals(page.getId(), PageServiceTest.TEST_PAGE_ID);
		Assert.assertEquals(page.getVersionId(), PageServiceTest.TEST_VERSION_ID);
		Assert.assertEquals(page.getDescription(), "Test Page");
		Assert.assertEquals(page.getPageHash(), ShaGenerator.calculateHash((Document)null));
		Assert.assertNull(page.getXmlContent());
	}

	public void validateModifiedPage(Page modifiedPage)
	{
		Assert.assertNotNull(modifiedPage);
		Assert.assertEquals(modifiedPage.getId(), PageServiceTest.TEST_PAGE_ID);
		Assert.assertEquals(modifiedPage.getVersionId(), PageServiceTest.TEST_VERSION_ID);
		Assert.assertEquals(modifiedPage.getDescription(), "Updated page");
		Assert.assertEquals(modifiedPage.getPageHash(), ShaGenerator.calculateHash((Document)null));
		Assert.assertNull(modifiedPage.getXmlContent());
	}
}
