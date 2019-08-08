package data.alto_site;

import java.io.File;

import static data.TestData.PATH_TO_DOWNLOADS_FOLDER;

public enum AltoSiteDocs {
    BROKEN_PDF("brokenfile.pdf", "1ExktG-ISJwZD1jyTCkha7oPDlmV8zOYI"),
    TWO_MB_PDF("2mb.pdf", "1YEAMBxbK9rRZAe5zZyXrI9HP0sEY_rnK"),
    MANY_PAGE_PDF("151pages.pdf", "1J_ZFAwZaX2R4VMeQb5z8WfpNeyKjvM9d"),
    TEN_MB_PDF("10.pdf", "18pWtkVLbPLR2UUEsNFgLDG99zSgmqu4P"),
    BIG_PDF("26mb.pdf", "1uEDfHuTtL4qUH8l8V0gmqBcbNp0pO1a-"),
    PROTECTED_PDF("test123R.pdf", "1303HXjpyNcPLZZ4pH6U7O7fZYJG5CULS"),
    SMALL_PDF("smallpdffile.pdf", "1s_THPRzau5Pp66biEjACLzqVo8aNFPdy"),
    TWO_MB_JPG("2mb.jpg", "1U_U2oKor-kcdlOAnYGGtqDaUk4vhT_ue"),
    BIG_JPG("27mb.jpg", "1SgunMrk8-mr3z4d4ATH45j0w7H9pVpMc"),
    SMALL_JPG("smalljpgfile.jpg", "1kvTWCZTeaUAggRvSZLU_7evpOMAwHJCw"),
    BROKEN_JPG("brokenfilejpg.jpg", "1wnDOthcPfRUfefhMw4QI2Vcryn1stVrY"),
    BROKEN_PNG("brokenfilepng.png", "1zOwhyTzc6WhvrTMCNB-T_YulY20cS2cW"),
    BIG_PNG("33mb.png", "1zYkww9eNwAzp-yhqnvjjPRQecYGSBLR2"),
    SMALL_PNG("smallpngfile.png", "12R6SZ-nPAI6xTqSGFkArKUV0EfwUHFj6"),
    THREE_MB_PNG("3mb.png", "1eHpanK7gES4MjMxj6DwW3RUwM6B179W8"),
    MANY_PAGE_PPT("151pages.ppt", "1XFRtY6uTHLPExx1r_lRrQO9_pLl737Ig"),
    MANY_PAGE_PPTX("153pages.pptx", "18uI-H4B6dT_tteWjGpFQRNgO924SsWe_"),
    BROKEN_PPT("brokenfileppt.ppt", "1gdXedWSR7qoErBoXy4KITZXFcTframWx"),
    BROKEN_PPTX("brokenfilepptx.pptx", "1iXl2vF4uM2s6ruGDHivT9vbewa76DNXg"),
    MEDIUM_PPTX("medimpptx.pptx", "1fFEnw4SsGqqVwRxwcDjXlGyx7gAuM8a2"),
    SMALL_PPT("smallpptfile.ppt", "1YP1o0Epqj71E7DoH751SXpblQzwa1Iz5"),
    SMALL_PPTX("smallpptxfile.pptx", "1tL2kSTLIFobjtRI-jhNbKqOIXgclP0s5"),
    BIG_PPT("26mb.ppt", "1sGDbASyzPnqj9nsGRO85zRPjyfShnpUX"),
    BIG_PPTX("26mb.pptx", "1-ybPtNuJSb7EfOd6xpB-4d3NaQi3qnSS"),
    BIG_DOC("28mb.doc", "1bfkwMiFMPnIxEQcZiFKm1YrgyVA77oIy"),
    BIG_DOCX("29mb.docx", "1YHvZMRb89993DmXWz4rRrsV_AbsWZsMK"),
    MANY_PAGE_DOC("151pages.doc", "13P8ZtLFzFbu3JuqhmmDQfFIdE9d2OLL"),
    MANY_PAGE_DOCX("157pages.docx", "1DwLZ4gPD1s_MJzOjCOeDReLP5IyuAQr0"),
    MEDIUM_DOC("medium.doc", "1RfODWtQ34_of9PThrWA5YnGUhPM35r8I"),
    SMALL_DOC("smalldocfile.doc", "11H4qz4rRjkhyUk7Ripmkd6yS5j5FPZ6n"),
    SMALL_DOCX("smalldocxfile.docx", "1mcvdzrfA15YeAWMTRYxedK2VYm2SIb5t"),
    BROKEN_DOC("brokenfiledoc.doc", "1vAOsAo2pdpNr6wNrjIsiuw9ScmuKLDkL"),
    BROKEN_DOCX("brokenfiledocx.docx", "1njgZjCfF51x29iWR3mJHXi1Vp9ke9vsV");

    private String name;
    private String id;

    AltoSiteDocs(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getFilePath() {
        return new File(PATH_TO_DOWNLOADS_FOLDER + File.separator + getName()).getAbsolutePath();
    }
}
