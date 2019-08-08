package utils;

import com.airslate.api.models.documents.Document;
import com.airslate.api.models.users.User;
import com.airslate.ws.EditorWsClient;
import io.qameta.allure.Step;
import websocket.editor.FieldBuilder;

import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

public class AirSlateWSEditor {

    private static AtomicInteger atomicInteger = new AtomicInteger(1);
    private EditorWsClient wsClient;

    public AirSlateWSEditor(EditorWsClient wsClient) {
        this.wsClient = wsClient;
    }

    public AirSlateWSEditor(String token, Document document, User adminUser, String orgDomain) throws URISyntaxException, InterruptedException {
        wsClient = new EditorWsClient(token, document.id, adminUser.id, orgDomain);
    }

    @Step
    public void addText(double x, double y, String name, boolean required) {
        FieldBuilder field = FieldBuilder.text(atomicInteger.getAndIncrement()).content(x, y, name, required);
        wsClient.send(field.getStringBody());
    }


    @Step
    public void addDate(double x, double y, String name, boolean required) {
        FieldBuilder field = FieldBuilder.date(atomicInteger.getAndIncrement()).content(x, y, name, required);
        wsClient.send(field.getStringBody());
    }

    @Step
    public void addNumber(double x, double y, String name, boolean required) {
        FieldBuilder field = FieldBuilder.number(atomicInteger.getAndIncrement()).content(x, y, name, required);
        wsClient.send(field.getStringBody());
    }

    @Step
    public void destroy() {
        try {
            wsClient.closeBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
