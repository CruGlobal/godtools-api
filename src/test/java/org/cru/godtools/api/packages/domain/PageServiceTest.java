package org.cru.godtools.api.packages.domain;

import org.cru.godtools.api.database.SqlConnectionProducer;
import org.cru.godtools.tests.UnittestDatabaseBuilder;
import org.sql2o.Connection;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryancarlson on 4/2/14.
 */
public class PageServiceTest
{
	UnittestDatabaseBuilder builder;
	PageServiceTestMockDataService mockData;

	Connection sqlConnection;
	PageService pageService;

	public static final UUID TEST_PAGE_ID = UUID.randomUUID();
	public static final UUID TEST_VERSION_ID = UUID.randomUUID();

	@BeforeClass
	public void setup()
	{
		builder = new UnittestDatabaseBuilder();
		builder.build();

		sqlConnection = SqlConnectionProducer.getTestSqlConnection();
		pageService = new PageService(sqlConnection);

		mockData = new PageServiceTestMockDataService();
		mockData.persistVersion(new VersionService(sqlConnection));
		mockData.persistPage(pageService);
	}

	@Test
	public void testSelectByVersionId()
	{
		List<Page> pages = pageService.selectByVersionId(TEST_VERSION_ID);

		Assert.assertEquals(pages.size(), 1);
		mockData.validatePage(pages.get(0));
	}

	@Test
	public void testSelectById()
	{
		Page page = pageService.selectById(TEST_PAGE_ID);

		mockData.validatePage(page);
	}

	@Test
	public void testSelectByFilename()
	{
		Page page = pageService.selectByFilename("page.xml");

		mockData.validatePage(page);
	}

	@Test
	public void testSelectAllPages()
	{
		List<Page> pages = pageService.selectAllPages();

		Assert.assertEquals(pages.size(), 1);
		mockData.validatePage(pages.get(0));
	}

	@Test
	public void testUpdate()
	{
		Connection nonAutoCommitSqlConnection1 = sqlConnection.getSql2o().beginTransaction();

		try
		{
			PageService nonAutoCommitPageService = new PageService(nonAutoCommitSqlConnection1);

			mockData.modifyPage(nonAutoCommitPageService);

			mockData.validateModifiedPage(nonAutoCommitPageService.selectById(TEST_PAGE_ID));
		}

		finally
		{
			nonAutoCommitSqlConnection1.rollback();
		}

	}

}
