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
                new ScType(ScType.ArcCommon),
                new ScType(),
                new ScType(ScType.ArcPosConstPerm),
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
                                                new ScType(ScType.ArcPosConstPerm),
                                                new ScType());
        while(iter3.next()){
            SctpIterator iter32 = sctpClient.iterate3(SctpIterator.Iterator3F_A_F,
                                                        iter3.value(2),
                                                        new ScType(ScType.ArcPosConstPerm),
                                                        game);
            if(iter32.next()){
                return this.get(iter3.value(2), ID);
            }
        }
        return "";
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
            game.setCompanyDevelop(this.get(iter.value(2), COMPANY_DEVELOP));
            game.setCompanyRelease(this.get(iter.value(2), COMPANY_RELEASE));
            game.setEngine(this.get(iter.value(2), ENGINE));
            game.setPlatform(this.get(iter.value(2), PLATFORM));
            game.setGenre(this.get3(iter.value(2), GENRE));
            game.setSetting(this.get3(iter.value(2), SETTING));
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

    public Game getGame(String name){
        Game game = new Game();
        game.setName(name);
        SctpIterator iter = sctpClient.iterate3(SctpIterator.Iterator3F_A_A,
                COMPUTER_GAME,
                new ScType(ScType.ArcPosConstPerm),
                new ScType(ScType.Node));
        while(iter.next()){
            if(name.equals(this.get(iter.value(2), ID))){
                game.setScAddr(iter.value(2).getValue());
                game.setName(this.get(iter.value(2), ID));
                game.setCompanyDevelop(this.get(iter.value(2), COMPANY_DEVELOP));
                game.setCompanyRelease(this.get(iter.value(2), COMPANY_RELEASE));
                game.setEngine(this.get(iter.value(2), ENGINE));
                game.setPlatform(this.get(iter.value(2), PLATFORM));
                game.setGenre(this.get3(iter.value(2), GENRE));
                game.setSetting(this.get3(iter.value(2), SETTING));
            }
        }
        System.out.println(game.getScAddr());
        return game;
    }

    public void connect(){
        sctpClient = new SctpClient();
        if(sctpClient.connect("localhost", 55770)){
            System.out.println("Connect Success");
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
        this.getGame("Paragon");

    }

    public static void main(String[] args) {
        GameRest gameRest = new GameRest();
        gameRest.connect();
    }
}
