package redhat.che.e2e.tests.selenium.ide;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.arquillian.graphene.Graphene.waitAjax;
import static redhat.che.e2e.tests.selenium.ide.Utils.constructPath;

public class ContextMenu {


    @Drone
    private WebDriver driver;

    @FindBy(id = "gwt-debug-contextMenu/newGroup")
    private WebElement contextMenu;

    /**
     * Opens context menu on an item with specified path. Item has to be visible
     * in project explorer and selected.
     *
     * @param pathToItem path to an item
     */
    public void openContextMenuOnItem(String... pathToItem) {
        String locator = "//div[@path='" + constructPath(pathToItem) + "']/div";
        new WebDriverWait(driver, Timeouts.REDRAW)
            .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
        WebElement node = driver.findElement(By.xpath(locator));
        node.click();
        Actions act = new Actions(driver);
        Action rClick = act.contextClick(node).build();
        rClick.perform();
        rClick.perform();
        waitOnContextMenu();
    }

    private void waitOnContextMenu() {
        waitAjax().withTimeout(30, SECONDS).until().element(contextMenu).is().visible();
    }

    private void waitForContextMenuItemAndClickOnIt(String item) {
        new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.id(item))).click();
    }

    /**
     * Selects context menu item with specified path. Context menu has to be opened in order
     * to select a specific context menu item.
     *
     * @param pathToContextMenuItem path to context menu item in context menu
     */
    public void selectContextMenuItem(String... pathToContextMenuItem) {
        for (String item : pathToContextMenuItem) {
            waitForContextMenuItemAndClickOnIt(item);
        }
        waitUntilContextMenuIsClosed();
    }


    private void waitUntilContextMenuIsClosed() {
        waitAjax().withTimeout(10, SECONDS).until().element(contextMenu).is().not().visible();
    }
}
