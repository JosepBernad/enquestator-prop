package analysis;

import java.util.Set;

public class Cluster {

    private Point centroid;
    private Set<Point> points;
    private Boolean hasChanges;

    public Cluster() {
        hasChanges = true;
    }

    public Point getCentroid() {
        return centroid;
    }

    public void setCentroid(Point centroid) {
        this.centroid = centroid;
    }

    public Set<Point> getPoints() {
        return points;
    }

    public void setPoints(Set<Point> points) {
        this.points = points;
    }

    public Boolean getHasChanges() {
        return hasChanges;
    }

    public void setHasChanges(Boolean hasChanges) {
        this.hasChanges = hasChanges;
    }
}
