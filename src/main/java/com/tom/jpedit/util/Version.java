package com.tom.jpedit.util;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Version {
  private final int major, minor, patch;

  @Override
  public String toString() {
    return "Version(" + major +
        "." + minor +
        "." + patch +
        "-" + addendum +
        ')';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
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
