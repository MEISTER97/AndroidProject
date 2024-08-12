package com.example.goodgameproject.utilities;

import java.io.Serializable;
import java.util.Arrays;

public class Account implements Serializable {
    private String profileName;
    private GameGenre[] selectedGenres;
    private Platform[] selectedPlatforms;

    public Account(String profileName, GameGenre[] selectedGenres, Platform[] selectedPlatforms) {
        this.profileName = profileName;
        this.selectedGenres = selectedGenres;
        this.selectedPlatforms = selectedPlatforms;
    }

    public enum GameGenre {
        RPG, ACTION, ADVENTURE, FIGHTING, PUZZLE, SURVIVAL, RACING, STRATEGY;

        public static GameGenre fromString(String text) {
            for (GameGenre genre : GameGenre.values()) {
                if (genre.name().equalsIgnoreCase(text)) {
                    return genre;
                }
            }
            throw new IllegalArgumentException("No GameGenre with text " + text + " found");
        }
    }

    public enum Platform {
        PC, CONSOLE, MOBILE;

        public static Platform fromString(String text) {
            for (Platform platform : Platform.values()) {
                if (platform.name().equalsIgnoreCase(text)) {
                    return platform;
                }
            }
            throw new IllegalArgumentException("No Platform with text " + text + " found");
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "profileName='" + profileName + '\'' +
                ", selectedGenres=" + Arrays.toString(selectedGenres) +
                ", selectedPlatforms=" + Arrays.toString(selectedPlatforms) +
                '}';
    }

    // Getters and setters
    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public GameGenre[] getSelectedGenres() {
        return selectedGenres;
    }

    public void setSelectedGenres(GameGenre[] selectedGenres) {
        this.selectedGenres = selectedGenres;
    }

    public Platform[] getSelectedPlatforms() {
        return selectedPlatforms;
    }

    public void setSelectedPlatforms(Platform[] selectedPlatforms) {
        this.selectedPlatforms = selectedPlatforms;
    }
}
