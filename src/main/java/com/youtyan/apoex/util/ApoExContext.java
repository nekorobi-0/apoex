package com.youtyan.apoex.util;

import com.youtyan.apoex.IApoExMekanism;

public class ApoExContext {
    public static final ThreadLocal<IApoExMekanism> MEKANISM_TILE = new ThreadLocal<>();
}
