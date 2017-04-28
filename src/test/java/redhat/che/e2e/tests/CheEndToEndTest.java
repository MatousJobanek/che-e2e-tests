/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
*/
package redhat.che.e2e.tests;

import org.apache.log4j.Logger;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import redhat.che.e2e.tests.provider.CheWorkspaceProvider;
import redhat.che.e2e.tests.resource.CheWorkspace;
import redhat.che.e2e.tests.resource.CheWorkspaceStatus;
import redhat.che.e2e.tests.selenium.ide.ContextMenu;
import redhat.che.e2e.tests.selenium.ide.Labels;
import redhat.che.e2e.tests.selenium.ide.Popup;
import redhat.che.e2e.tests.selenium.ide.ProjectExplorer;
import redhat.che.e2e.tests.selenium.ide.TestResultsView;
import redhat.che.e2e.tests.service.CheWorkspaceService;

import static org.jboss.arquillian.graphene.Graphene.waitAjax;
import static redhat.che.e2e.tests.Constants.CHE_STARTER_URL;
import static redhat.che.e2e.tests.Constants.CREATE_WORKSPACE_REQUEST_JSON;
import static redhat.che.e2e.tests.Constants.KEYCLOAK_TOKEN;
import static redhat.che.e2e.tests.Constants.OPENSHIFT_MASTER_URL;
import static redhat.che.e2e.tests.Constants.OPENSHIFT_NAMESPACE;
import static redhat.che.e2e.tests.Constants.PATH_TO_TEST_FILE;
import static redhat.che.e2e.tests.Constants.PRESERVE_WORKSPACE_PROPERTY_NAME;

@RunWith(Arquillian.class)
public class CheEndToEndTest {

	private static final Logger logger = Logger.getLogger(CheEndToEndTest.class);

	@FindBy(id = "gwt-debug-editorPartStack-contentPanel")
	private CodeEditorFragment codeEditor;

	@FindBy(id = "gwt-debug-projectTree")
	private ProjectExplorer explorer;

	@FindBy(id = "gwt-debug-infoPanel")
	private Popup testsPopup;

	@FindBy(className = "ide-page-frame")
	private WebElement idePageFrameElement;

	@FindByJQuery("tbody:has(tr[id^='gwt-debug-contextMenu'])")
	private ContextMenu contextMenu;

	@FindBy(id = "gwt-debug-MenuItem/profileGroup-true")
	private WebElement profileMenuItem;

	@FindBy(id = "topmenu/Profile/Preferences")
	private WebElement profilePereferencesMenuItem;

	@FindByJQuery("div:contains('Preferences'):contains('Java Compiler'):last")
	private PreferencesWindow preferencesWindow;

	@Drone
	private WebDriver driver;

	private static CheWorkspace workspace;

	@Test
	public void testCreateWorkspaceAndHandleProject() {
		logger.info("Calling che starter to create a new workspace on OpenShift");

		workspace = CheWorkspaceProvider.createCheWorkspace(CHE_STARTER_URL, OPENSHIFT_MASTER_URL,
				KEYCLOAK_TOKEN, CREATE_WORKSPACE_REQUEST_JSON, OPENSHIFT_NAMESPACE);
		logger.info("Workspace successfully created.");

		logger.info("Waiting until workspace starts");
		CheWorkspaceService.waitUntilWorkspaceGetsToState(workspace, CheWorkspaceStatus.RUNNING.getStatus());

		driver.get(workspace.getIdeLink());
		// Running single JUnit Class
		logger.info("Running JUnit test class on the project");
		runTest();
		checkTestResults();
	}

	private void runTest() {
		explorer.selectItem(PATH_TO_TEST_FILE);
		contextMenu.openContextMenuOnItem(PATH_TO_TEST_FILE);
		contextMenu.selectContextMenuItem(Labels.ContextMenuItem.TEST, Labels.ContextMenuItem.JUNIT_CLASS);

		// Wait until tests finish
		testsPopup.waitUntilExists(Popup.RUNNING_TESTS_TITLE, 20);
		testsPopup.waitWhileExists(Popup.RUNNING_TESTS_TITLE, 100);
		testsPopup.waitUntilExists(Popup.SUCCESSFULL_TESTS_TITLE, 10);

		explorer.openPomXml();
		codeEditor.writeDependency();
		codeEditor.verifyAnnotationErrorIsPresent();

		waitAjax().until().element(profileMenuItem).is().visible();
		profileMenuItem.click();
		waitAjax().until().element(profilePereferencesMenuItem).is().visible();
		profilePereferencesMenuItem.click();
	}

	private void checkTestResults() {
		TestResultsView testView = new TestResultsView(driver);
		testView.open();
		testView.assertLatestTestRunPassed();
	}
	
	private static boolean shouldNotDeleteWorkspace() {
		String preserveWorkspaceProperty = System.getProperty(PRESERVE_WORKSPACE_PROPERTY_NAME);
		if (preserveWorkspaceProperty == null) {
			return false;
		}
		if (preserveWorkspaceProperty.toLowerCase().equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	@AfterClass
	public static void cleanUp() {
		if (workspace != null && !shouldNotDeleteWorkspace()) {
			if (CheWorkspaceService.getWorkspaceStatus(workspace).equals(CheWorkspaceStatus.RUNNING.getStatus())) {
				logger.info("Stopping workspace");
				CheWorkspaceService.stopWorkspace(workspace);
			}
			logger.info("Deleting workspace");
			CheWorkspaceService.deleteWorkspace(workspace);
		} else {
			logger.info("Property to preserve workspace is set to true, skipping workspace deletion");
		}
	}
}
