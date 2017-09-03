package example.es;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

/**
 *
 * @author ss
 */
public class DamageTypes implements EntityComponent {
    
    public static final String PHYSICAL = "physical";
    public static final String POISEN = "poisen";
    public static final String FIRE = "fire";
    public static final String LIGHTNING = "lightning";
    public static final String ICE = "ice";

    public static DamageType physical(EntityData ed, EntityId owner, double minimumDamage, double maximumDamage) {
        return DamageType.create(PHYSICAL, ed, owner, minimumDamage, maximumDamage);
    }

    public static DamageType poisen(EntityData ed, EntityId owner, double minimumDamage, double maximumDamage) {
        return DamageType.create(POISEN, ed, owner, minimumDamage, maximumDamage);
    }
    
    public static DamageType fire(EntityData ed, EntityId owner, double minimumDamage, double maximumDamage) {
        return DamageType.create(FIRE, ed, owner, minimumDamage, maximumDamage);
    }
        
    public static DamageType lightning(EntityData ed, EntityId owner, double minimumDamage, double maximumDamage) {
        return DamageType.create(LIGHTNING, ed, owner, minimumDamage, maximumDamage);
    }
    
    public static DamageType ice(EntityData ed, EntityId owner, double minimumDamage, double maximumDamage) {
        return DamageType.create(ICE, ed, owner, minimumDamage, maximumDamage);
    }
    
}
