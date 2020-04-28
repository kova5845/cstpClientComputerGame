package home.com.smarthome.sctp;

public class Game {
    private String scAddr;
    private String name;
    private String genre;
    private String setting;
    private String companyDevelop;
    private String companyRelease;
    private String engine;
    private String platform;

    public Game(){

    }

    public Game(String csAddr, String name, String genre, String setting, String companyDevelop, String companyRelease, String engine, String platform) {
        this.scAddr = csAddr;
        this.name = name;
        this.genre = genre;
        this.setting = setting;
        this.companyDevelop = companyDevelop;
        this.companyRelease = companyRelease;
        this.engine = engine;
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public String getCompanyDevelop() {
        return companyDevelop;
    }

    public void setCompanyDevelop(String companyDevelop) {
        this.companyDevelop = companyDevelop;
    }

    public String getCompanyRelease() {
        return companyRelease;
    }

    public void setCompanyRelease(String companyRelease) {
        this.companyRelease = companyRelease;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getScAddr() {
        return scAddr;
    }

    public void setScAddr(String scAddr) {
        this.scAddr = scAddr;
    }
}
