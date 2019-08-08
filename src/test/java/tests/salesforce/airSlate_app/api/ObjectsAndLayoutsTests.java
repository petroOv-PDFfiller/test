package tests.salesforce.airSlate_app.api;

import api.salesforce.entities.airslate.ObjectsView;
import api.salesforce.entities.airslate.ResponseBody;
import api.salesforce.util.HttpResponseUtils;
import listeners.WebTestListener;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import tests.salesforce.SalesforceAirSlateBaseTest;

import java.util.List;

import static core.check.Check.checkEquals;

@Listeners({WebTestListener.class})
public class ObjectsAndLayoutsTests extends SalesforceAirSlateBaseTest {

    @Test
    public void admin_getLayouts() {
        SoftAssert softAssert = new SoftAssert();
        HttpResponse layoutsResponse = salesforceAirslateApi.objectsView().getObjectView(ObjectsView.View.LAYOUTS, null);
        ResponseBody<List<ObjectsView>> layoutsResponseBody = HttpResponseUtils.parseResponseWithCollection(layoutsResponse, ObjectsView.class);
        checkEquals(layoutsResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");

        List<ObjectsView> layouts = layoutsResponseBody.getData();
        layouts.forEach(layout -> softAssert.assertNotNull(layout.getFullName(), "Layout fullName"));
        softAssert.assertAll();
    }

    @Test
    public void admin_getObjects() {
        SoftAssert softAssert = new SoftAssert();
        HttpResponse listViewResponse = salesforceAirslateApi.objectsView().getObjectView(ObjectsView.View.LIST_VIEW, null);
        ResponseBody<List<ObjectsView>> listViewResponseBody = HttpResponseUtils.parseResponseWithCollection(listViewResponse, ObjectsView.class);
        checkEquals(listViewResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");

        List<ObjectsView> listViews = listViewResponseBody.getData();
        listViews.forEach(listView -> softAssert.assertNotNull(listView.getFullName(), "ListView fullName"));
        softAssert.assertAll();
    }
}
