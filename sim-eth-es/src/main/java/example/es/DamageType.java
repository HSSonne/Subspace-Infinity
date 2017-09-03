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
public class DamageType  implements EntityComponent {
    
    private EntityId owner;
    private int type;
    private double minDamage;
    private double maxDamage;
 
    protected DamageType() {
    }
    
    public DamageType( int type, EntityId owner, double min, double max) {
        this.owner = owner;
        this.type = type;
        this.maxDamage = max;
        this.minDamage = min;
    }
    
    public static DamageType create( String typeName, EntityData ed, EntityId owner, double minDmg, double maxDmg ) {
        return new DamageType(ed.getStrings().getStringId(typeName, true), owner, minDmg, maxDmg);
    }
    
    public int getType() {
        return type;
    }
    
    public String getTypeName( EntityData ed ) {
        return ed.getStrings().getString(type);                 
    }
 
    @Override   
    public String toString() {
        return getClass().getSimpleName() + "[type=" + type + ", dmg=" + minDamage + "<->" + maxDamage + "]";
    }     
    
    public double getMinDamage(){
        return this.minDamage;
    }
    
    public double getMaxDamage(){
        return this.maxDamage;
    }
    
    public EntityId getOwner(){
        return this.owner;
    }
}
