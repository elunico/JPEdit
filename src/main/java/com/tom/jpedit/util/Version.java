package com.tom.jpedit.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Version implements Comparable<Version> {

    @Override
    public int compareTo(@NotNull Version o) {
        var m = Integer.compare(major, o.major);
        if (m == 0) {
            var n = Integer.compare(minor, o.minor);
            if (n == 0) {
                return Integer.compare(patch, o.patch);
            }
            return n;
        }
        return m;
    }

    private final int major, minor, patch;
    @Nullable
    private final String addendum;

    public Version(int major, int minor, int patch) {
        this(major, minor, patch, null);
    }

    public Version(int major, int minor, int patch, String addendum) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.addendum = addendum;
    }

    @Override
    public String toString() {
        return "v" + major +
                "." + minor +
                "." + patch +
                "-" + addendum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Version version = (Version) o;
        return major == version.major && minor == version.minor && patch == version.patch && Objects.equals(
                addendum,
                version.addendum
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, addendum);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public @Nullable String getAddendum() {
        return addendum;
    }

    public boolean isAtLeast(int major, int minor, int patch) {
        return (this.major > major) || (this.major == major && this.minor > minor) || (this.major == major && this.minor == minor && this.patch >= patch);
    }

    public boolean isAtLeast(int major, int minor) {
        return (this.major > major) || (this.major == major && this.minor > minor);
    }
}
