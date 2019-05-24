package org.dreamcat.common.hc.test;

import lombok.Getter;

/**
 * Create by tuke on 2019-02-01
 */
@Getter
public class Version {
    // 0.1.0bugfixed1
    private final int majorVersion;
    private final int minorVersion;
    private final int microVersion;
    private final Type type;
    private final String specialVersion;

    public Version(int majorVersion, int minorVersion, int microVersion, Type type) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.microVersion = microVersion;
        this.type = type;

        this.specialVersion = String.format("%d.%d.%d%s",
                majorVersion, microVersion, microVersion, type.getValue());
    }

    @Getter
    public enum Type {
        ALPHA("a", "alpha", "alpha"),
        BETA("b", "beta", "beta"),
        RC2("rc1", "rc1", "releaseCandidate1"),
        RC1("rc2", "rc2", "releaseCandidate2"),
        RELEASE("r", "", "release");

        /**
         * @see #name
         */
        private final String value;
        private final String shortName;
        private final String name;
        private final String longName;

        Type(String shortName, String name, String longName) {
            this.shortName = shortName;
            this.name = name;
            this.longName = longName;

            this.value = name;
        }

        public String getValue() {
            return value;
        }

    }
}
