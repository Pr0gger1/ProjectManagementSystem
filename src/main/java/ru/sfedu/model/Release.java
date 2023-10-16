package ru.sfedu.model;

import java.util.Date;

public class Release extends ManagementEntity {
    private Date releaseDate;

    Release(String name, String description, Date releaseDate) {
        super(name, description);
        this.releaseDate = releaseDate;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
