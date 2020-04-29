package home.com.smarthome.sctp;

import java.util.ArrayList;

public class GameRest {
    private static ScAddr COMPUTER_GAME;
    private static ScAddr GENRE;
    private static ScAddr SETTING;
    private static ScAddr COMPANY_DEVELOP;
    private static ScAddr COMPANY_RELEASE;
    private static ScAddr PLATFORM;
    private static ScAddr ENGINE;
    private static ScAddr ID;

    private SctpClient sctpClient;

    public String get(ScAddr game, ScAddr elem){
        SctpIterator iter5 = sctpClient.iterate5(SctpIterator.Iterator5F_A_A_A_F,
                game,
                new ScType(),
                new ScType(),
                new ScType(),
                elem);
        if(iter5.next()){
            if(sctpClient.getLinkContent(iter5.value(2)) != null){
                return SctpClient.ByteBufferToString(sctpClient.getLinkContent(iter5.value(2)));
            }
            else{
                return this.get(iter5.value(2), ID);
            }
        }
        return "";
    }

    public String get3(ScAddr game, ScAddr elem){
        SctpIterator iter3 = sctpClient.iterate3(SctpIterator.Iterator3F_A_A,
                                                elem,
                                                new ScType(),
                                                new ScType());
        while(iter3.next()){
            SctpIterator iter32 = sctpClient.iterate3(SctpIterator.Iterator3F_A_F,
                                                        iter3.value(2),
                                                        new ScType(),
                                                        game);
            if(iter32.next()){
                return this.get(iter3.value(2), ID);
            }
        }
        return "";
    }

    public Game getGameID(String name){
        Game game = new Game();
        game.setName(name);
        ScAddr[] nameAddr = sctpClient.findLinksByContent(SctpClient.ByteBufferFromString(name));
        if(nameAddr == null)
            return null;
        SctpIterator iter5 = sctpClient.iterate5(SctpIterator.Iterator5A_A_F_A_F,
                new ScType(),
                new ScType(),
                nameAddr[0],
                new ScType(),
                ID);

        game.setScAddr(iter5.value(0).getValue());
        game.setName(this.get(iter5.value(0), ID));
        game.setCompanyDevelop(this.get(iter5.value(0), COMPANY_DEVELOP));
        game.setCompanyRelease(this.get(iter5.value(0), COMPANY_RELEASE));
        game.setEngine(this.get(iter5.value(0), ENGINE));
        game.setPlatform(this.get(iter5.value(0), PLATFORM));
        game.setGenre(this.get3(iter5.value(0), GENRE));
        game.setSetting(this.get3(iter5.value(0), SETTING));
        return game;
    }

    public ArrayList<Game> getGames(){
        ArrayList<ScAddr> arrOfGamesAddr = new ArrayList<>();
        ArrayList<Game> games = new ArrayList<>();
        SctpIterator iter = sctpClient.iterate3(SctpIterator.Iterator3F_A_A,
                                                COMPUTER_GAME,
                                                new ScType(ScType.ArcPosConstPerm),
                                                new ScType(ScType.Node));
        while(iter.next()){
            Game game = new Game();
            game.setScAddr(iter.value(2).getValue());
            game.setName(this.get(iter.value(2), ID));
//            game.setCompanyDevelop(this.get(iter.value(2), COMPANY_DEVELOP));
//            game.setCompanyRelease(this.get(iter.value(2), COMPANY_RELEASE));
//            game.setEngine(this.get(iter.value(2), ENGINE));
//            game.setPlatform(this.get(iter.value(2), PLATFORM));
//            game.setGenre(this.get3(iter.value(2), GENRE));
//            game.setSetting(this.get3(iter.value(2), SETTING));
            System.out.println(game.getScAddr() +
                    game.getName() +
                    game.getCompanyDevelop() +
                    game.getCompanyRelease() +
                    game.getEngine() +
                    game.getPlatform() +
                    game.getGenre() +
                    game.getSetting());
            if(!game.getName().equals("")){
                games.add(game);
            }
        }
        return games;
    }

    public ScAddr findNodeById(ScAddr addr, String name) {
        SctpIterator iter3 = sctpClient.iterate3(SctpIterator.Iterator3F_A_A,
                                                addr,
                                                new ScType(),
                                                new ScType());
        while (iter3.next()) {
            if(name.equals(this.get(iter3.value(2), ID))){
                return iter3.value(2);
            }
        }
        return null;
    }

    public Game getGame(String name){
        System.out.println("name of game is:" + name);
        Game game = new Game();
        game.setName(name);
        ScAddr scGame = this.findNodeById(COMPUTER_GAME, name);
        if(scGame == null)
            return null;
        game.setScAddr(scGame.getValue());
        game.setName(this.get(scGame, ID));
        game.setCompanyDevelop(this.get(scGame, COMPANY_DEVELOP));
        game.setCompanyRelease(this.get(scGame, COMPANY_RELEASE));
        game.setEngine(this.get(scGame, ENGINE));
        game.setPlatform(this.get(scGame, PLATFORM));
        game.setGenre(this.get3(scGame, GENRE));
        game.setSetting(this.get3(scGame, SETTING));
        return game;
    }

    public void addToName(ScAddr game, String nodeName){
        ScAddr name = sctpClient.createLink();
        sctpClient.setLinkContent(name, SctpClient.ByteBufferFromString(nodeName));
        ScAddr nameArc = sctpClient.createArc(game, name, new ScType(ScType.ArcCommon));
        sctpClient.createArc(ID, nameArc, new ScType(ScType.ArcPosConstPerm));
    }

    public void addToGame(ScAddr game, ScAddr node, ScAddr nrel, String name){
        if(node == null) {
            System.out.println(name);
            return;
        }
        ScAddr nameArc = sctpClient.createArc(game, node, new ScType(ScType.ArcCommon));
        sctpClient.createArc(nrel, nameArc, new ScType(ScType.ArcPosConstPerm));
        this.addToName(node, name);
    }

    public ScAddr setGame(Game game) {
        System.out.println(game.getScAddr() +
                game.getName() +
                game.getCompanyDevelop() +
                game.getCompanyRelease() +
                game.getEngine() +
                game.getPlatform() +
                game.getGenre() +
                game.getSetting());
        ScAddr conEngine = sctpClient.findElementBySystemIdentifier("game_engine");
        ScAddr conPlatform = sctpClient.findElementBySystemIdentifier("game_platform");
        ScAddr conCompany = sctpClient.findElementBySystemIdentifier("concept_company");
        ScAddr scGame = sctpClient.createNode(new ScType(ScType.Node));
        sctpClient.setSystemIdentifier(scGame, "computer_game_" + game.getName());
        this.addToName(scGame, game.getName());
        ScAddr genre = this.findNodeById(GENRE, game.getGenre());
        sctpClient.createArc(genre, scGame, new ScType());
        ScAddr setting = this.findNodeById(SETTING, game.getSetting());
        sctpClient.createArc(setting, scGame, new ScType());
        ScAddr engine = this.findNodeById(conEngine, game.getEngine());
        this.addToGame(scGame, engine, ENGINE, game.getEngine());
        ScAddr companyDevelop = this.findNodeById(conCompany, game.getCompanyDevelop());
        this.addToGame(scGame, companyDevelop, COMPANY_DEVELOP, game.getCompanyDevelop());
        ScAddr companyRelease = this.findNodeById(conCompany, game.getCompanyRelease());
        this.addToGame(scGame, companyRelease, COMPANY_RELEASE, game.getCompanyRelease());
        ScAddr platform = this.findNodeById(conPlatform, game.getPlatform());
        this.addToGame(scGame, platform, PLATFORM, game.getPlatform());
        sctpClient.createArc(COMPUTER_GAME, scGame, new ScType(ScType.ArcPosConstPerm));
        return scGame;
    }

    public boolean connect(){
        boolean flag = false;
        sctpClient = new SctpClient();
        if(sctpClient.connect("localhost", 55770)){
            flag = true;
        }
        COMPUTER_GAME = sctpClient.findElementBySystemIdentifier("concept_computer_game");
        GENRE = sctpClient.findElementBySystemIdentifier("concept_game_genre");
        SETTING = sctpClient.findElementBySystemIdentifier("concept_setting");
        COMPANY_DEVELOP = sctpClient.findElementBySystemIdentifier("nrel_developer");
        COMPANY_RELEASE = sctpClient.findElementBySystemIdentifier("nrel_publisher");
        PLATFORM = sctpClient.findElementBySystemIdentifier("nrel_platform");
        ENGINE = sctpClient.findElementBySystemIdentifier("nrel_game_engine");
        ID = sctpClient.findElementBySystemIdentifier("nrel_main_idtf");

//        this.getGames();
        Game game = this.getGame("test_game_5");
        System.out.println(game.getScAddr() +
                game.getName() +
                game.getCompanyDevelop() +
                game.getCompanyRelease() +
                game.getEngine() +
                game.getPlatform() +
                game.getGenre() +
                game.getSetting());
//        Game game = new Game(0,
//                "test_game3",
//                "МОБА",
//                "fantasy",
//                "Valve",
//                "Valve",
//                "Source 2",
//                "Windows");
//        System.out.println(this.setGame(game).getValue());
        return flag;
    }

    public static void main(String[] args) {
        GameRest gameRest = new GameRest();
        gameRest.connect();
    }
}
