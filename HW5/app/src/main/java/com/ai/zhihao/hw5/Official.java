package com.ai.zhihao.hw5;

/**
 * Created by zhihaoai on 3/24/18.
 */

public class Official {

    private String office;
    private String name;
    private String party;

    public Official(String office, String name, String party) {
        this.office = office;
        this.name = name;
        this.party = party;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }
}
