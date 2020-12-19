package api;

public class GeoLocation implements geo_location{
    private double x;
    private double y;
    private double z;

    public GeoLocation(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    public GeoLocation(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GeoLocation(geo_location l){
        this.x = l.x();
        this.y = l.y();
        this.z = l.z();
    }


    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }



    @Override
    public double distance(geo_location p) {
        double dx = this.x() - p.x();
        double dy = this.y() - p.y();
        double dz = this.z() - p.z();
        double t = (dx*dx+dy*dy+dz*dz);
        return Math.sqrt(t);
    }

    @Override
    public String toString() {
        return x+","+y+","+z;
    }
}
