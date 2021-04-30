package dartmouth.cs.qiyaozuo;

public class ManualEntryModel {
    private String title, data;

    public ManualEntryModel(String title, String data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setData(String data) {
        this.data = data;
    }
}

