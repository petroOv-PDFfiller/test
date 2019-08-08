package data.airslate;

public class AirSlateTestData {
    public static final String AIRSLATE_WINSTRING = "airSlate";
    public static final String AIRSLATE_EDITOR_FRAME = "airslate-jsfiller";
    private static final String devHeadDomain = "airslate-dev";
    private static final String rcHeadDomain = "airslate-rc.xyz";
    private static final String prodHeadDomain = "airslate.com";
    private static final String stageHeadDomain = "airslate-stage.xyz";
    private static final String prodWsUrl = "wss://desk1-websocket.airslate.com";
    private static final String rcWsUrl = "wss://desk1-websocket.airslate-rc.xyz";
    private static final String stageWsUrl = "wss://desk1-websocket.airslate-stage.xyz";
    private static final String devWSUrl = "wss://airslate-dev";

    public enum Environments {
        DEV00(devHeadDomain + "00.xyz", "api." + devHeadDomain + "00.xyz", "oauth." + devHeadDomain + "00.xyz", devWSUrl + "00.xyz"),
        DEV01(devHeadDomain + "01.xyz", "api." + devHeadDomain + "01.xyz", "oauth." + devHeadDomain + "01.xyz", devWSUrl + "01.xyz"),
        DEV02(devHeadDomain + "02.xyz", "api." + devHeadDomain + "02.xyz", "oauth." + devHeadDomain + "02.xyz", devWSUrl + "02.xyz"),
        DEV03(devHeadDomain + "03.xyz", "api." + devHeadDomain + "03.xyz", "oauth." + devHeadDomain + "03.xyz", devWSUrl + "03.xyz"),
        DEV04(devHeadDomain + "04.xyz", "api." + devHeadDomain + "04.xyz", "oauth." + devHeadDomain + "04.xyz", devWSUrl + "04.xyz"),
        DEV05(devHeadDomain + "05.xyz", "api." + devHeadDomain + "05.xyz", "oauth." + devHeadDomain + "05.xyz", devWSUrl + "05.xyz"),
        DEV06(devHeadDomain + "06.xyz", "api." + devHeadDomain + "06.xyz", "oauth." + devHeadDomain + "06.xyz", devWSUrl + "06.xyz"),
        DEV07(devHeadDomain + "07.xyz", "api." + devHeadDomain + "07.xyz", "oauth." + devHeadDomain + "07.xyz", devWSUrl + "07.xyz"),
        DEV08(devHeadDomain + "08.xyz", "api." + devHeadDomain + "08.xyz", "oauth." + devHeadDomain + "08.xyz", devWSUrl + "08.xyz"),
        DEV09(devHeadDomain + "09.xyz", "api." + devHeadDomain + "09.xyz", "oauth." + devHeadDomain + "09.xyz", devWSUrl + "09.xyz"),
        DEV10(devHeadDomain + "10.xyz", "api." + devHeadDomain + "10.xyz", "oauth." + devHeadDomain + "10.xyz", devWSUrl + "10.xyz"),
        DEV11(devHeadDomain + "11.xyz", "api." + devHeadDomain + "11.xyz", "oauth." + devHeadDomain + "11.xyz", devWSUrl + "11.xyz"),
        DEV12(devHeadDomain + "12.xyz", "api." + devHeadDomain + "12.xyz", "oauth." + devHeadDomain + "12.xyz", devWSUrl + "12.xyz"),
        DEV13(devHeadDomain + "13.xyz", "api." + devHeadDomain + "13.xyz", "oauth." + devHeadDomain + "13.xyz", devWSUrl + "13.xyz"),
        DEV14(devHeadDomain + "14.xyz", "api." + devHeadDomain + "14.xyz", "oauth." + devHeadDomain + "14.xyz", devWSUrl + "14.xyz"),
        DEV15(devHeadDomain + "15.xyz", "api." + devHeadDomain + "15.xyz", "oauth." + devHeadDomain + "15.xyz", devWSUrl + "15.xyz"),
        DEV16(devHeadDomain + "16.xyz", "api." + devHeadDomain + "16.xyz", "oauth." + devHeadDomain + "16.xyz", devWSUrl + "16.xyz"),
        DEV17(devHeadDomain + "17.xyz", "api." + devHeadDomain + "17.xyz", "oauth." + devHeadDomain + "17.xyz", devWSUrl + "17.xyz"),
        DEV18(devHeadDomain + "18.xyz", "api." + devHeadDomain + "18.xyz", "oauth." + devHeadDomain + "18.xyz", devWSUrl + "18.xyz"),
        DEV19(devHeadDomain + "19.xyz", "api." + devHeadDomain + "19.xyz", "oauth." + devHeadDomain + "19.xyz", devWSUrl + "19.xyz"),
        DEV20(devHeadDomain + "20.xyz", "api." + devHeadDomain + "20.xyz", "oauth." + devHeadDomain + "20.xyz", devWSUrl + "20.xyz"),
        DEV21(devHeadDomain + "21.xyz", "api." + devHeadDomain + "21.xyz", "oauth." + devHeadDomain + "21.xyz", devWSUrl + "21.xyz"),
        DEV22(devHeadDomain + "22.xyz", "api." + devHeadDomain + "22.xyz", "oauth." + devHeadDomain + "22.xyz", devWSUrl + "22.xyz"),
        DEV23(devHeadDomain + "23.xyz", "api." + devHeadDomain + "23.xyz", "oauth." + devHeadDomain + "23.xyz", devWSUrl + "23.xyz"),
        DEV24(devHeadDomain + "24.xyz", "api." + devHeadDomain + "24.xyz", "oauth." + devHeadDomain + "24.xyz", devWSUrl + "24.xyz"),
        DEV25(devHeadDomain + "25.xyz", "api." + devHeadDomain + "25.xyz", "oauth." + devHeadDomain + "25.xyz", devWSUrl + "25xyz"),
        DEV26(devHeadDomain + "26.xyz", "api." + devHeadDomain + "26.xyz", "oauth." + devHeadDomain + "26.xyz", devWSUrl + "26.xyz"),
        DEV27(devHeadDomain + "27.xyz", "api." + devHeadDomain + "27.xyz", "oauth." + devHeadDomain + "27.xyz", devWSUrl + "27.xyz"),
        DEV28(devHeadDomain + "28.xyz", "api." + devHeadDomain + "28.xyz", "oauth." + devHeadDomain + "28.xyz", devWSUrl + "28.xyz"),
        DEV29(devHeadDomain + "29.xyz", "api." + devHeadDomain + "29.xyz", "oauth." + devHeadDomain + "29.xyz", devWSUrl + "29.xyz"),
        DEV30(devHeadDomain + "30.xyz", "api." + devHeadDomain + "30.xyz", "oauth." + devHeadDomain + "30.xyz", devWSUrl + "30.xyz"),
        DEV31(devHeadDomain + "31.xyz", "api." + devHeadDomain + "31.xyz", "oauth." + devHeadDomain + "31.xyz", devWSUrl + "31.xyz"),
        DEV32(devHeadDomain + "32.xyz", "api." + devHeadDomain + "32.xyz", "oauth." + devHeadDomain + "32.xyz", devWSUrl + "32.xyz"),
        DEV33(devHeadDomain + "33.xyz", "api." + devHeadDomain + "33.xyz", "oauth." + devHeadDomain + "33.xyz", devWSUrl + "33.xyz"),
        DEV34(devHeadDomain + "34.xyz", "api." + devHeadDomain + "34.xyz", "oauth." + devHeadDomain + "34.xyz", devWSUrl + "34.xyz"),
        DEV35(devHeadDomain + "35.xyz", "api." + devHeadDomain + "35.xyz", "oauth." + devHeadDomain + "35.xyz", devWSUrl + "35.xyz"),
        DEV36(devHeadDomain + "36.xyz", "api." + devHeadDomain + "36.xyz", "oauth." + devHeadDomain + "36.xyz", devWSUrl + "36.xyz"),
        STAGE(stageHeadDomain, "api." + stageHeadDomain, "oauth." + stageHeadDomain, stageWsUrl),
        RC(rcHeadDomain, "api." + rcHeadDomain, "oauth." + rcHeadDomain, rcWsUrl),
        PROD(prodHeadDomain, "api." + prodHeadDomain, "oauth." + prodHeadDomain, prodWsUrl);

        private String domain;

        private String domainAPI;

        private String domainOAuth;

        private String wsUrl;

        Environments(String domain, String domainAPI, String domainOAuth, String wsUrl) {
            this.domain = domain;
            this.domainAPI = domainAPI;
            this.domainOAuth = domainOAuth;
            this.wsUrl = wsUrl;
        }

        public String getDomain() {
            return domain;
        }

        public String getAPIDomain() {
            return domainAPI;
        }

        public String getDomainOAuth() {
            return domainOAuth;
        }

        public String getWsUrl() {
            return wsUrl;
        }
    }

    public static class CreateAccountFields {
        public static final String FIRST_NAME = "First Name";
        public static final String LAST_NAME = "Last Name";
        public static final String EMAIL = "Email";
        public static final String PASSWORD = "Password";
    }

    public static class AirslateAddonTypes {
        public static final String PACKET_OPENED = "Packet opened";
        public static final String PACKET_SUBMITTED = "Packet submitted";
        public static final String AFTER_PACKET_SUBMITTED = "After packet submitted";
    }
}
