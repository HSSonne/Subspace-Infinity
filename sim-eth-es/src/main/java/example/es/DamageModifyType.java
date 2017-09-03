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
public class DamageModifyType implements EntityComponent {
 
    private EntityId owner;
    private int type;
    private double scale;
 
    protected DamageModifyType() {
    }
    
    public DamageModifyType( int type, EntityId owner, double scale) {
        this.owner = owner;
        this.type = type;
        this.scale = scale;
    }
    
    public static DamageModifyType create( String typeName, EntityId owner, EntityData ed, double scale) {
        return new DamageModifyType(ed.getStrings().getStringId(typeName, true), owner, scale);
    }
    
    public int getType() {
        return type;
    }
    
    public String getTypeName( EntityData ed ) {
        return ed.getStrings().getString(type);                 
    }
 
    @Override   
    public String toString() {
        return getClass().getSimpleName() + "[type=" + type + ", scale=" + scale + "]";    }     
    
    public double getScale(){
        return this.scale;
    }
    
    public EntityId getOwner(){
        return this.owner;
    }
}