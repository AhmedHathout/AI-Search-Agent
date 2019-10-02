package main.entities;

import java.util.Objects;

/**
 * Contains the x and y coordinates of any thing on the grid.
 * The y actually represents horizontal movement and x represents the vertical one
 */
public class Location {

    int x;
    int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isAdjacent(Location other) {
        return ((getX() == other.getX() + 1 || getX() == other.getX() - 1) && getY() == other.getY())
                || ((getY() == other.getY() + 1 || getY() == other.getY() - 1) && getX() == other.getX());
    }

    public Location moveUp() {
        return new Location(getX() - 1, getY());
    }

    public Location moveDown() {
        return new Location(getX() + 1, getY());
    }

    public Location moveLeft() {
        return new Location(getX(), getY() - 1);
    }

    public Location moveRight() {
        return new Location (getX(), getY() + 1);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return x == location.x &&
                y == location.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
