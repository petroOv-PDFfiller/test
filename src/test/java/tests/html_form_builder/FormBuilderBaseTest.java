package tests.html_form_builder;

import com.airslate.api.models.documents.Document;
import com.google.gson.JsonObject;
import com.thirdparty.html_form_builder.models.FormPages;
import com.thirdparty.html_form_builder.models.HTMLForm;
import tests.AirSlateBaseTest;
import utils.StringMan;

import java.util.Map;

public class FormBuilderBaseTest extends AirSlateBaseTest {

    Document initFormBuilderDocumentData(String documentName) {
        Document formBuilderDocument = new Document();
        formBuilderDocument.type = "HTML_FORMS";
        formBuilderDocument.name = documentName;
        return formBuilderDocument;
    }

    HTMLForm initFormData(String formName, String documentId, String flowID, FormPages formPages) {
        HTMLForm form = new HTMLForm();
        form.name = formName;
        form.id = documentId;
        form.flowId = flowID;
        form.setJson(formPages);
        return form;
    }

    JsonObject getFillJson(Map<String, String> fields) {
        JsonObject jsonObject = new JsonObject();
        for (String key : fields.keySet()) {
            jsonObject.addProperty(key, fields.get(key));
        }
        return jsonObject;
    }

    protected String getFlowName() {
        return "FormBuilderFlow" + StringMan.getRandomString(5);
    }

    protected String getApiUrl() {
        return testData.apiUrl.replace("api", "integrations").concat("/html-form-builder");
    }
}
