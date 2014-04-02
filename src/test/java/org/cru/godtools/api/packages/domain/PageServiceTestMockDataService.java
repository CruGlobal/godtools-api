package org.cru.godtools.api.packages.domain;

import org.testng.Assert;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class PageServiceTestMockDataService
{
	public void persistPage(PageService pageService)
	{
		Page page = new Page();
		page.setId(PageServiceTest.TEST_PAGE_ID);
		page.setOrdinal(0);
		page.setVersionId(PageServiceTest.TEST_VERSION_ID);
		page.setDescription("Test Page");
		page.setFilename("page.xml");
		page.setPageHash("abalcdsf");
		page.setXmlContent(null);

		pageService.insert(page);
	}

	public void modifyPage(PageService pageService)
	{
		Page page = new Page();
		page.setId(PageServiceTest.TEST_PAGE_ID);
		page.setOrdinal(1);
		page.setVersionId(PageServiceTest.TEST_VERSION_ID);
		page.setDescription("Updated page");
		page.setFilename("page.xml");
		page.setPageHash("sdflkjsd");
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
		Assert.assertEquals(page.getOrdinal(), (Integer)0);
		Assert.assertEquals(page.getDescription(), "Test Page");
		Assert.assertEquals(page.getFilename(), "page.xml");
		Assert.assertEquals(page.getPageHash(), "abalcdsf");
		Assert.assertNull(page.getXmlContent());
	}

	public void validateModifiedPage(Page modifiedPage)
	{
		Assert.assertNotNull(modifiedPage);
		Assert.assertEquals(modifiedPage.getId(), PageServiceTest.TEST_PAGE_ID);
		Assert.assertEquals(modifiedPage.getVersionId(), PageServiceTest.TEST_VERSION_ID);
		Assert.assertEquals(modifiedPage.getOrdinal(), (Integer)1);
		Assert.assertEquals(modifiedPage.getDescription(), "Updated page");
		Assert.assertEquals(modifiedPage.getFilename(), "page.xml");
		Assert.assertEquals(modifiedPage.getPageHash(), "sdflkjsd");
		Assert.assertNull(modifiedPage.getXmlContent());
	}
}
