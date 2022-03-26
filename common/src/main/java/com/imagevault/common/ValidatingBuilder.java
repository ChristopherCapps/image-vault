package com.imagevault.common;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;

/**
 * Provides javax.validation support on top of the builder pattern.
 *
 * @param <T> the type that is built
 * @param <B> the type of the builder itself
 */
public abstract class ValidatingBuilder<T, B extends ValidatingBuilder<T, B>> {

  protected T object;

  protected ValidatingBuilder() {
    object = newObject();
  }

  protected final B set(final Consumer<T> setter) {
    setter.accept(object);
    return self();
  }

  protected final <V> Collection<V> collection(final List<V> value) {
    return Collections.unmodifiableList(value);
  }

  protected final <V> Collection<V> collection(final Collection<V> value) {
    if (value instanceof List) {
      return collection((List<V>) value);
    } else if (value instanceof Set) {
      return collection((Set<V>) value);
    }
    return value == null ? Collections.emptyList() : Collections.unmodifiableCollection(value);
  }

  protected final <V> Collection<V> collection(final Set<V> value) {
    return Collections.unmodifiableSet(value);
  }

  @NotNull
  protected abstract T newObject();

  @SuppressWarnings("unchecked")
  @NotNull
  protected B self() {
    return (B) this;
  }

  public T build() {
    validate();
    return object;
  }

  private void validate() {
    final Set<ConstraintViolation<T>> violations = Collections.emptySet(); //validator.validate(object);
    if (violations.isEmpty()) {
      return;
    }
    final String messages = String
        .join("\n", violations.stream().map(v -> v.toString()).collect(Collectors.toList()));
    throw new IllegalArgumentException("Failed to build " + object.getClass() + "\n" + messages);
  }

}
