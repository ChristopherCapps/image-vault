package com.imagevault.core;

import java.net.URI;
import java.time.ZonedDateTime;

public interface Media {

  MediaType getType();

  ZonedDateTime getCreatedDate();

  URI getLocation();

  String getFingerprint();

  enum MediaType {
    JPEG,
    AVI,
    HEIF
  }

}
