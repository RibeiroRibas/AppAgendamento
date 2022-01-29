package br.com.beautystyle.model;

public enum TypeOfReport {

    MONTHLY("Mensal"), DAILY("Diário"), BYPERIOD("Por período");

    private final String description;

    TypeOfReport(String description) {

        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
