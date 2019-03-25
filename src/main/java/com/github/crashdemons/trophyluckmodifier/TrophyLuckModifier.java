/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.trophyluckmodifier;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author crash
 */
public class TrophyLuckModifier extends JavaPlugin implements Listener {
    private boolean PHEnabled=false;
    private boolean MTEnabled=false;
    
    public boolean hasPlayerheads(){return PHEnabled;}
    public boolean hasMiningtrophies(){return MTEnabled;}
    
    private boolean isPHAvailable(){
        if(this.getServer().getPluginManager().getPlugin("PlayerHeads") != null){
            return classExists("org.shininet.bukkit.playerheads.events.MobDropHeadEvent");
        }
        return false;
    }
    private boolean isMTAvailable(){
        if(this.getServer().getPluginManager().getPlugin("MiningTrophies") != null){
            return classExists("com.github.crashdemons.miningtrophies.events.BlockDropTrophyEvent");
        }
        return false;
    }
    
    
    private boolean classExists(String name){
        try {
            Class.forName( name );
            return true;
        } catch( ClassNotFoundException e ) {
            return false;
        }
    }
    
    private boolean pluginInit(){
        this.PHEnabled=isPHAvailable();
        this.MTEnabled=isMTAvailable();
        
        if(PHEnabled || MTEnabled){
            if(PHEnabled) getLogger().info("PlayerHeads support detected");
            if(MTEnabled) getLogger().info("MiningTrophies support detected");
            return true;
        }else{
            getLogger().warning("Neither PlayerHeads or MiningTrophies plugins are present - disabling plugin");
            this.getServer().getPluginManager().disablePlugin(this);
            return false;
        }
    }
    
    @Override
    public void onLoad(){
        
    }
    
    @Override
    public void onEnable(){
        getLogger().info("Enabling...");
        if(!pluginInit()) return;
        saveDefaultConfig();
        reloadConfig();
        if(PHEnabled){
            getServer().getPluginManager().registerEvents(new PHListener(this), this);
        }
        if(MTEnabled){
            getServer().getPluginManager().registerEvents(new MTListener(this), this);
        }
        getLogger().info("Enabled.");
    }
    
    @Override
    public void onDisable(){
        getLogger().info("Disabling...");
        saveConfig();
        getLogger().info("Disabled.");
    }
    
    public double getRelevantLuckRate(RollType type){
        switch(type){
            case MININGTROPHIES:
                return getConfig().getDouble("mining-luck-rate");
            case PLAYERHEADS:
                return getConfig().getDouble("head-luck-rate");
        }
        return 0.0;
    }
    
    public void modifyRoll(RollEventAdapter adaptedEvent){
        if(adaptedEvent.getAlwaysRewarded()) return;//don't modify rolls that always-reward already.
        if(adaptedEvent.getEffectiveDropRate()==0.0) return;//don't modify 0-rate rolls.
        
        Entity entity = adaptedEvent.getEntity();
        int luck = 0;
        if(entity instanceof LivingEntity){
            LivingEntity lentity = (LivingEntity) entity;
            for(PotionEffect effect : lentity.getActivePotionEffects()){
                if(effect.getType().equals(PotionEffectType.LUCK)){
                    luck+=effect.getAmplifier()+1;
                    //getLogger().info(" luck "+luck+" detected.");
                }
                if(effect.getType().equals(PotionEffectType.UNLUCK)){
                    luck-=effect.getAmplifier()+1;
                    //getLogger().info(" bad luck "+(-luck)+" detected.");
                }
            }
            if(luck==0) return;//don't modify results without any sort of luck effect!
            double luckrate = getRelevantLuckRate(adaptedEvent.getType());
            double newDropRate = adaptedEvent.getEffectiveDropRate()*(1 + luckrate*luck);
            double dropRoll = adaptedEvent.getEffectiveDropRoll();
            //getLogger().info(" luckrate="+luckrate);
            //getLogger().info(" droprate: "+adaptedEvent.getEffectiveDropRate()+ " -> "+newDropRate);
            //getLogger().info(" roll: "+dropRoll);
            adaptedEvent.setSuccess( dropRoll < newDropRate );
            //getLogger().info(" success: "+adaptedEvent.succeeded());
        }
    }

}
