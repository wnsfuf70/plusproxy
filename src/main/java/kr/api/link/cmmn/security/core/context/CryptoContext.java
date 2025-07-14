package kr.api.link.cmmn.security.core.context;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;

import kr.api.link.cmmn.security.constant.CryptoType;
import kr.api.link.cmmn.security.constant.EncodingType;
import kr.api.link.cmmn.security.constant.SignMode;

public class CryptoContext {

    private final Map<ContextKey<?>, Object> attr = new HashMap<>();

    public <T> void put(ContextKey<T> key, T value) {
    	attr.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ContextKey<T> key) {
        return (T) attr.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(ContextKey<T> key, T def) {
        return (T) attr.getOrDefault(key, def);
    }

    public boolean contains(ContextKey<?> key) {
        return attr.containsKey(key);
    }
    
    public static final class Util {

        private Util() {}

        public static boolean isRequestCryptoEnabled(CryptoContext ctx) {
            if (ctx.contains(CryptoKeys.USE_CRYPTO_REQUEST)) {
                return ctx.getOrDefault(CryptoKeys.USE_CRYPTO_REQUEST, false);
            }
            return ctx.getOrDefault(CryptoKeys.USE_CRYPTO, false);
        }

        public static boolean isResponseCryptoEnabled(CryptoContext ctx) {
            if (ctx.contains(CryptoKeys.USE_CRYPTO_RESPONSE)) {
                return ctx.getOrDefault(CryptoKeys.USE_CRYPTO_RESPONSE, false);
            }
            return ctx.getOrDefault(CryptoKeys.USE_CRYPTO, false);
        }

        public static boolean isSignEnabled(CryptoContext ctx) {
            return getSignMode(ctx).isEnabled();
        }

        public static SignMode getSignMode(CryptoContext ctx) {
            boolean value = ctx.getOrDefault(CryptoKeys.USE_GPKI_SIGN, true);
            return value ? SignMode.ENABLED : SignMode.DISABLED;
        }

        public static EncodingType getEncodingType(CryptoContext ctx) {
            String value = ctx.getOrDefault(CryptoKeys.ENCODING_TYPE, "BASE64");
            return EncodingType.from(value);
        }

        public static CryptoType getCryptoType(CryptoContext ctx) {
            String raw = ctx.getOrDefault(CryptoKeys.CRYPTO_TYPE, "GPKI");
            return CryptoType.from(raw);
        }

        public static boolean isForceRefresh(CryptoContext ctx) {
            return ctx.getOrDefault(CryptoKeys.FORCE_REFRESH, false);
        }

        public static long getForceRefreshTTL(CryptoContext ctx) {
            return ctx.getOrDefault(CryptoKeys.FORCE_REFRESH_TIME_MILLIS, 600_000L); // default: 10분
        }

        public static <T extends Enum<T>> T getEnum(CryptoContext ctx, ContextKey<String> key, Class<T> enumClass, T defaultValue) {
            String raw = ctx.getOrDefault(key, defaultValue.name());
            try {
                return Enum.valueOf(enumClass, raw.toUpperCase());
            } catch (Exception e) {
                return defaultValue;
            }
        }
        
        public static void traceContext(CryptoContext ctx , Logger log) {
            if (!log.isDebugEnabled()) return;

            Map<String, Object> sorted = new TreeMap<>();
            for (Map.Entry<ContextKey<?>, Object> entry : ctx.attr.entrySet()) {
                sorted.put(entry.getKey().name(), entry.getValue());
            }

            log.debug("📦 CryptoContext (YAML style)");
            sorted.forEach((k, v) -> log.debug("  {}: {}", k, v));
        }
        
    }
    
}