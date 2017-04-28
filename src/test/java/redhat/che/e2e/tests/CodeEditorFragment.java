package redhat.che.e2e.tests;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class CodeEditorFragment {

    @Root
    private WebElement rootElement;

    @FindByJQuery("div:contains('vertx-core') > span:last")
    private WebElement emptyElementAfterVertxDep;

    private String dependencyToAdd =
        "\n</dependency> \n"
            + "<dependency>\n"
            + "<groupId>ch.qos.logback</groupId>\n"
            + "<artifactId>logback-core</artifactId>\n"
            + "<version>1.1.10</version>";

    @Drone
    private WebDriver browser;

    public void findDependencies() {
        Graphene.waitModel().until().element(emptyElementAfterVertxDep).is().present();
        Actions actions = new Actions(browser);
        actions.moveToElement(emptyElementAfterVertxDep);
        actions.click();
        actions.sendKeys(new String[] {dependencyToAdd});
        actions.build().perform();

        System.out.println(emptyElementAfterVertxDep.getText());
    }
}
