/*
 * $Id$
 * 
 * Copyright (c) 2016, Simsilica, LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the 
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package example.sim;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.mathd.*;
import com.simsilica.es.*;
import example.GameConstants;
import example.PhysicsConstants;
import example.ViewConstants;

import example.es.*;
import java.util.HashSet;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.Torque;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Vector2;

/**
 * Utility methods for creating the common game entities used by the simulation.
 * In cases where a game entity may have multiple specific componnets or
 * dependencies used to create it, it can be more convenient to have a
 * centralized factory method. Especially if those objects are widely used. For
 * entities with only a few components or that are created by one system and
 * only consumed by one other, then this is not necessarily true.
 *
 */
public class GameEntities {

    //TODO: All constants should come through the parameters - for now, they come from the constants
    //TODO: All parameters should be dumb types and should be the basis of the complex types used in the backend
    public static EntityId createShip(EntityId parent, String shipType, EntityData ed) {
        EntityId result = ed.createEntity();
        Name name = ed.getComponent(parent, Name.class);
        ed.setComponent(result, name);
        ed.setComponents(result,
                ObjectTypes.ship(ed),
                ShipType.create(shipType, ed),
                new MassProperties(1 / PhysicsConstants.SHIPMASS),
                new PhysicsShape(new BodyFixture(new Circle(PhysicsConstants.SHIPSIZERADIUS)))); //for Dyn4j physics

        ed.setComponent(result, new HitPoints(GameConstants.SHIPHEALTH));

        return result;
    }

    public static EntityId createGravSphere(Vec3d pos, double radius, EntityData ed) {
        EntityId result = ed.createEntity();
        ed.setComponents(result, ObjectTypes.gravSphereType(ed),
                new Position(pos, new Quatd().fromAngles(-Math.PI * 0.5, 0, 0), 0.0), //TODO: Test angle                
                new SphereShape(radius, new Vec3d()));
        return result;
    }

    public static EntityId createBounty(Vec3d pos, EntityData ed) {
        EntityId result = ed.createEntity();
        ed.setComponents(result, ObjectTypes.bounty(ed),
                new Position(pos, new Quatd(), 0f),
                new Bounty(GameConstants.BOUNTYVALUE),
                new PhysicsShape(new BodyFixture(new Circle(PhysicsConstants.BOUNTYSIZERADIUS))),
                new SphereShape(ViewConstants.BOUNTYSIZE, new Vec3d()),
                new Decay(GameConstants.BOUNTYDECAY));
        return result;
    }

    public static EntityId createBountySpawner(Vec3d pos, double radius, EntityData ed) {
        EntityId result = ed.createEntity();
        ed.setComponents(result,
                //Possible to add model if we want the players to be able to see the spawner
                new Position(pos, new Quatd(), 0f),
                new Spawner(GameConstants.BOUNTYMAXCOUNT, Spawner.SpawnType.Bounties));
        return result;
    }

    public static EntityId createBomb(Vec3d location, Quatd quatd, double rotation, Vector2 linearVelocity, long decayMillis, EntityData ed) {
        EntityId lastBomb = ed.createEntity();
        
        ed.setComponents(lastBomb, ObjectTypes.bomb(ed),
                new Position(location, quatd, rotation),
                new PhysicsVelocity(new Vector2(linearVelocity.x, linearVelocity.y)),
                new Decay(decayMillis),
                new MassProperties(1 / PhysicsConstants.BOMBMASS), //for physics
                new PhysicsShape(new BodyFixture(new Circle(PhysicsConstants.BOMBSIZERADIUS)))); //for Dyn4j physics

        return lastBomb;
    }

    public static EntityId createDelayedBomb(Vec3d location, Quatd quatd, double rotation, Vector2 linearVelocity, long decayMillis, long scheduledMillis, HashSet<EntityComponent> delayedComponents, EntityData ed) {

        EntityId lastDelayedBomb = GameEntities.createBomb(location, quatd, rotation, linearVelocity, decayMillis, ed);

        ed.setComponents(lastDelayedBomb, new Delay(scheduledMillis, delayedComponents));

        return lastDelayedBomb;
    }

    public static EntityId createBullet(Vec3d location, Quatd quatd, double rotation, Vector2 linearVelocity, long decayMillis, EntityData ed) {
        EntityId lastBomb = ed.createEntity();
        ed.setComponents(lastBomb, ObjectTypes.bullet(ed),
                new Position(location, quatd, rotation),
                new PhysicsVelocity(new Vector2(linearVelocity.x, linearVelocity.y)),
                new Decay(decayMillis),
                new MassProperties(1 / PhysicsConstants.BULLETMASS), //for physics
                new PhysicsShape(new BodyFixture(new Circle(PhysicsConstants.BULLETSIZERADIUS)))); //for Dyn4j physics

        return lastBomb;
    }

    public static EntityId createArena(int arenaId, EntityData ed) { //TODO: Should have Position as a parameter in case we want to create more than one arena
        EntityId lastArena = ed.createEntity();

        ed.setComponents(lastArena, ObjectTypes.arena(ed),
                new Position(new Vec3d(0, 0, arenaId), new Quatd(), 0f)
        );

        return lastArena;
    }

    public static EntityId createMapTile(MapTileType mapTileType, Vec3d location, EntityData ed, Convex c) {
        EntityId lastMapTile = ed.createEntity();

        ed.setComponents(lastMapTile, ObjectTypes.mapTile(ed),
                new Position(location, new Quatd(), 0f),
                mapTileType, //TODO: Should be parameterized
                new MassProperties(PhysicsConstants.MAPTILEMASS), //for Physics
                new PhysicsShape(new BodyFixture(c))); //for physics - for now, only 1 by 1 tiles created (square)

        return lastMapTile;
    }

    //Explosion is for now only visual, so only object type and position
    public static EntityId createExplosion2(Vec3d location, Quatd quat, EntityData ed) {
        EntityId lastExplosion = ed.createEntity();

        ed.setComponents(lastExplosion,
                ObjectTypes.explosion2(ed),
                new Position(location, quat, 0f),
                new Decay(ViewConstants.EXPLOSION2DECAY));

        return lastExplosion;
    }

    public static EntityId createWormhole(Vec3d location, double radius, Vec3d targetLocation, double targetAreaRadius, double force, EntityData ed) {
        EntityId lastWormhole = ed.createEntity();

        ed.setComponents(lastWormhole,
                ObjectTypes.wormhole(ed),
                new Position(location, new Quatd(), 0f),
                new MassProperties(PhysicsConstants.WORMHOLEMASS),
                new GravityWell(radius, force),
                new Warp(targetAreaRadius, targetLocation),
                new PhysicsShape(new BodyFixture(new Circle(PhysicsConstants.WORMHOLESIZERADIUS))));

        return lastWormhole;
    }

    public static EntityId createAttack(EntityId owner, String attackType, EntityData ed) {
        EntityId lastAttack = ed.createEntity();
        ed.setComponents(lastAttack,
                new Attack(owner),
                AttackType.create(attackType, ed));

        return lastAttack;
    }

    public static EntityId createForce(EntityId owner, Force force, Torque torque, EntityData ed) {
        EntityId lastForce = ed.createEntity();
        ed.setComponents(lastForce,
                new PhysicsForce(owner, force, torque));

        return lastForce;
    }

    public static EntityId createOver5(Vec3d location, double radius, Vec3d targetLocation, double targetAreaRadius, double force, EntityData ed) {
        EntityId lastWormhole = ed.createEntity();

        ed.setComponents(lastWormhole,
                ObjectTypes.over5(ed),
                new Position(location, new Quatd(), 0f),
                new MassProperties(PhysicsConstants.OVER5MASS),
                new GravityWell(radius, force),
                new Warp(targetAreaRadius, targetLocation),
                new PhysicsShape(new BodyFixture(new Circle(PhysicsConstants.OVER5SIZERADIUS))));

        return lastWormhole;
    }

    //Rotating asteroid
    public static EntityId createOver1(Vec3d location, EntityData ed) {
        EntityId lastWormhole = ed.createEntity();

        ed.setComponents(lastWormhole,
                ObjectTypes.over1(ed),
                new Position(location, new Quatd(), 0f),
                new MassProperties(PhysicsConstants.OVER1MASS),
                new PhysicsShape(new BodyFixture(new Circle(PhysicsConstants.OVER1SIZERADIUS))));

        return lastWormhole;
    }
}
