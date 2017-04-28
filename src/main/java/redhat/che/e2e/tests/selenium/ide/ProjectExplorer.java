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
package redhat.che.e2e.tests.selenium.ide;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static redhat.che.e2e.tests.selenium.ide.Utils.constructPath;

/**
 * Project explorer located in Che Web IDE.
 * 
 * @author mlabuda
 */
public class ProjectExplorer {

	@Drone
	private WebDriver driver;

	@Root
	private WebElement projectExplorerRoot;

	@FindByJQuery("div:contains('pom.xml'):last")
	private WebElement pomXmlItem;

	@FindBy(id = "gwt-debug-refreshSelectedPath")
	private WebElement refreshButton;

	/**
	 * Waits until an item with specic text is visible in project explorer
	 * 
	 * @param text text of an item
	 */
	private void waitUntilItemIsVisible(String text) {
		String locator = String.format("//div[@id='gwt-debug-projectTree']//div[text()='%s']", text);
		new WebDriverWait(driver, Timeouts.REDRAW)
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
	}

	/**
	 * Opens an item in project explorer. Parent items have to be expanded and
	 * visible.
	 * 
	 * @param path path to an item
	 */
	private void openItem(String path) {
		String locator = "//div[@path='%s']/div";
		By findBy = By.xpath(String.format(locator, path));

		WebDriverWait wait = new WebDriverWait(driver, Timeouts.REDRAW);
		wait.until(ExpectedConditions.visibilityOfElementLocated(findBy));
		WebElement item = wait.until(ExpectedConditions.elementToBeClickable(findBy));
		try {
			item.click();
			new Actions(driver).doubleClick(item).perform();
		} catch (StaleElementReferenceException ex) {
			refresh();
			item.click();
			new Actions(driver).doubleClick(item).perform();
		}
		revalidate();
	}

	/**
	 * Opens an item in project explorer with specific path.
	 * 
	 * @param path
	 *            path to an item
	 * @return path to the item
	 */
	private void openItem(String... path) {
		String tmpPath = "";
		for (String text : path) {
			tmpPath += "/" + text;
			openItem(tmpPath);
		}
	}

	/**
	 * Selects item with specified path which consists of visible texts of items in project explorer.
	 * 
	 * @param pathToItem path to an item
	 */
	public void selectItem(String... pathToItem) {
		openItem(pathToItem);
		selectItem(constructPath(pathToItem));
	}

	/**
	 * Selects an item in project explorer. Parent items have to be expanded and
	 * visible.
	 * 
	 * @param path
	 *            path to an item
	 */
	private void selectItem(String path) {
		String locator = "//div[@path='" + path + "']/div";
		String[] items = path.split("/");
		waitUntilItemIsVisible(items[items.length - 1]);
		WebElement item = driver.findElement(By.xpath(locator));
		item.click();
	}


	/**
	 * Refreshes project explorer.
	 */
	public void refresh() {
		new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(refreshButton)).click();
	}

	/**
	 * Revalidates project explorer tree.
	 */
	private void revalidate() {
		new WebDriverWait(driver, Timeouts.REVALIDATING).until(ExpectedConditions.visibilityOf(projectExplorerRoot));
	}

	/**
	 * Opens pom.xml file
	 */
	public void openPomXml() {
		Graphene.waitModel().until().element(pomXmlItem).is().visible();
		new Actions(driver).doubleClick(pomXmlItem).perform();
	}
}
