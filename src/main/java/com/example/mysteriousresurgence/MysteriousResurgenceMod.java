package com.example.mysteriousresurgence;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("mysteriousresurgence")
public class MysteriousResurgenceMod {
    public static final String MOD_ID = "mysteriousresurgence";
    public static final Logger LOGGER = LogManager.getLogger();

    public MysteriousResurgenceMod() {
        LOGGER.info("神秘复苏模组加载成功！");
    }
}
