/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.trophyluckmodifier;

import com.github.crashdemons.trophyluckmodifier.modifiers.DropRateModifier;
import com.github.crashdemons.trophyluckmodifier.modifiers.DropRateModifierType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
        //getLogger().info("Enabling...");
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
        
        getLogger().info(" Config head-luck-rate: "+getConfig().getDouble("head-luck-rate"));
        getLogger().info(" Config mining-luck-rate: "+getConfig().getDouble("mining-luck-rate"));
        getLogger().info(" Config debug-luck-attribute: "+(getConfig().getBoolean("debug-luck-attribute")?"Y":"N"));
        getLogger().info(" Config debug-luck-increase: "+(getConfig().getBoolean("debug-luck-increase")?"Y":"N"));
    }
    
    @Override
    public void onDisable(){
        //getLogger().info("Disabling...");
        //saveConfig();
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
    
    public DropRateModifier getModification(RollEventAdapter adaptedEvent){
        if(adaptedEvent.getAlwaysRewarded()) return DropRateModifier.None;//don't modify rolls that always-reward already.
        if(adaptedEvent.getEffectiveDropRate()==0.0) return DropRateModifier.None;//don't modify 0-rate rolls.
        
        Entity entity = adaptedEvent.getEntity();
        double luck = 0;
        if(entity instanceof LivingEntity){
            LivingEntity lentity = (LivingEntity) entity;
            
            AttributeInstance attrib = lentity.getAttribute(Attribute.GENERIC_LUCK);
            if(attrib!=null){
                luck = attrib.getValue();
                if(getConfig().getBoolean("debug-luck-attribute"))
                    getLogger().info("Luck adef:"+attrib.getDefaultValue()+" abase:"+attrib.getBaseValue()+" aval:"+attrib.getValue());
            }
            
            if(luck==0) return DropRateModifier.None;//don't modify results without any sort of luck effect!
            double luckrate = getRelevantLuckRate(adaptedEvent.getType());
            
            DropRateModifier mod = new DropRateModifier(DropRateModifierType.ADD_MULTIPLE_PER_LEVEL,luckrate,(int)luck);
            
            double newDropRate = adaptedEvent.getEffectiveDropRate()*(1 + luckrate*luck);
            double dropRoll = adaptedEvent.getEffectiveDropRoll();
            
            adaptedEvent.setSuccess( dropRoll < newDropRate );
            if(getConfig().getBoolean("debug-luck-increase")){
                getLogger().info(" luckrate="+luckrate);
                getLogger().info("   droprate: "+adaptedEvent.getEffectiveDropRate()+ " -> "+newDropRate);
                getLogger().info("   roll: "+dropRoll);
                getLogger().info("   success: "+adaptedEvent.succeeded());
            }
            
            return mod;
        }
        return DropRateModifier.None;
    }


}
