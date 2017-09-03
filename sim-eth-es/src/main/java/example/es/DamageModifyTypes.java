/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.es;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

/**
 *
 * @author ss
 */
public class DamageModifyTypes implements EntityComponent {
    
    public static final String PHYSICAL = "physical";
    public static final String POISEN = "poisen";
    public static final String FIRE = "fire";
    public static final String LIGHTNING = "lightning";
    public static final String ICE = "ice";

        
    public static DamageModifyType physical(EntityData ed, EntityId owner, double scale) {
        return DamageModifyType.create(PHYSICAL, owner, ed, scale);
    }

    public static DamageModifyType poisen(EntityData ed, EntityId owner, double scale) {
        return DamageModifyType.create(POISEN, owner, ed, scale);
    }
    
    public static DamageModifyType fire(EntityData ed, EntityId owner, double scale) {
        return DamageModifyType.create(FIRE, owner, ed, scale);
    }
        
    public static DamageModifyType lightning(EntityData ed, EntityId owner, double scale) {
        return DamageModifyType.create(LIGHTNING, owner, ed, scale);
    }
    
    public static DamageModifyType ice(EntityData ed, EntityId owner, double scale) {
        return DamageModifyType.create(ICE, owner, ed, scale);
    }
    
}