package redhat.che.e2e.tests;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.arquillian.graphene.Graphene.waitAjax;

public class CodeEditorFragment {

    @Root
    private WebElement rootElement;

    @FindByJQuery("div:contains('vertx-core') > span:last")
    private WebElement emptyElementAfterVertxDep;

    @FindByJQuery("div.annotation.error > div.annotationHTML.error:last")
    private WebElement annotationError;

    private String dependencyToAdd =
        "\n</dependency> \n"
            + "<dependency>\n"
            + "<groupId>ch.qos.logback</groupId>\n"
            + "<artifactId>logback-core</artifactId>\n"
            + "<version>1.1.10</version>";

    @Drone
    private WebDriver browser;

    public void writeDependency() {
        waitAjax().withTimeout(5, SECONDS).until().element(emptyElementAfterVertxDep).is().present();
        Actions actions = new Actions(browser);
        actions.moveToElement(emptyElementAfterVertxDep);
        actions.click();
        actions.sendKeys(dependencyToAdd);
        actions.build().perform();
    }

    public void verifyAnnotationErrorIsPresent(){
        waitAjax()
            .withTimeout(10, SECONDS)
            .withMessage("The annotation error should be visible")
            .until()
            .element(annotationError)
            .is()
            .present();
    }
}
