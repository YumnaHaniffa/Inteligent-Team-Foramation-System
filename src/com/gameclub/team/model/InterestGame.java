package com.gameclub.team.model;

//Used instead of storing lists , to maintain usability of options and format
public enum InterestGame {
    Chess,
    Valorant,
    Dota2,
    FIFA,
    Basketball,
    CS_GO;

    @Override
    public String toString() {
        switch(this){
            case Chess:
                return "Chess";
            case Valorant:
                return  "Valorant";
            case Dota2:
                return  "Dota2";
            case FIFA:
                return  "FIFA";
            case Basketball:
                return  "Basketball";
            case CS_GO:
                return  "CS_GO";
            default:
                return this.name();
        }
    }


}
