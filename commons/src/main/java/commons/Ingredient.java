package commons;

import java.util.Objects;
import java.util.UUID;

public class Ingredient {

    private UUID id;
    private String name;
    private double proteinPer100g;
    private double fatPer100g;
    private double carbsPer100g;

    public Ingredient(String name, double proteinPer100g, double fatPer100g, double carbsPer100g) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.proteinPer100g = proteinPer100g;
        this.fatPer100g = fatPer100g;
        this.carbsPer100g = carbsPer100g;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getProteinPer100g() {
        return proteinPer100g;
    }

    public double getFatPer100g() {
        return fatPer100g;
    }

    public double getCarbsPer100g() {
        return carbsPer100g;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + getId().toString() +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ingredient that = (Ingredient) obj;
        return id == that.id && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
    
}
