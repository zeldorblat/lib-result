package org.zeldorblat.lib.result;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A monad representing a result that is in exactly one of
 * two states: success or failure. A success {@code Result} encapsulates
 * a single (possibly null) {@code Object} of class {@code T}, and a
 * failure {@code Result} encapsulates a single, non-null {@code Object}
 * of class {@code E} representing an error.
 *
 * <p>The monad itself is immutable to the extent that
 * the encapsulated value or error is also immutable.
 *
 * <p>Implementing classes need only define the {@link #fold(Function, Function) fold()}
 * method as all other methods can be derived from its implementation.
 * Implementers may choose to override additional methods as desired,
 * but are responsible for ensuring the immutability requirement.
 *
 * <p>The {@code Result} interface contains two concrete inner
 * static classes: {@link SuccessResult} and {@link FailureResult}.
 * It is certainly possible to create other implementations of the
 * {@code Result} interface; in practice, however, usages of {@code Result}
 * are generally expected to rely on the implementation here.
 * Obtaining new instances of {@code Result} should (almost) always be done
 * using the {@link #success(Object) success()} and {@link #failure(Object) failure()}
 * static factory methods.
 *
 * @param <T> the class of the encapsulated value
 * @param <E> the class of the encapsulated error
 */
public interface Result<T, E> {

	/**
	 * If success, apply {@code successMapper} to the encapsulated value
	 * and return the answer.
	 *
	 * If failure, apply {@code failureMapper} to the encapsulated error
	 * and return the answer.
	 *
	 * <p>Implementing classes need only define this
	 * method as all other methods can be derived from
	 * the implementation of {@code fold()}.
	 * Implementers may choose to override additional
	 * methods as desired.
	 *
	 * @param <R> the class of the value returned
	 * 			  by {@code successMapper} and {@code failureMapper}
	 * @param successMapper transform to apply to the encapsulated value
	 * @param failureMapper transform to apply to the encapsulated error
	 * @return the result of successMapper or failureMapper
	 * @throws NullPointerException if {@code successMapper} is null
	 * 								or {@code failureMapper} is null
	 */
	public <R> R fold(
			Function<? super T, ? extends R> successMapper,
			Function<? super E, ? extends R> failureMapper);

	/**
	 * If success, return {@code false}.
	 *
	 * If failure, return {@code true}.
	 *
	 * @return {@code true} if this is a failure {@code Result}
	 * @see #isSuccess()
	 */
	public default boolean isFailure() {
		return this.fold(t -> false, e -> true);
	}

	/**
	 * If success, return {@code true}.
	 *
	 * If failure, return {@code false}.
	 *
	 * @return {@code true} if this is a success {@code Result}
	 * @see #isFailure()
	 */
	public default boolean isSuccess() {
		return this.fold(t -> true, e -> false);
	}

	/**
	 * If success, return {@code null}.
	 *
	 * If failure, return the encapsulated error.
	 *
	 * @return the encapsulated error
	 * @see #getOrThrow(Function)
	 */
	public default E errorOrNull() {
		return this.fold(t -> null, identity());
	}

	/**
	 * If success, return the encapsulated value.
	 *
	 * If failure, apply {@code failureMapper} to the encapsulated error
	 * and return the answer.
	 *
	 * @param failureMapper transform to apply to the encapsulated error
	 * @return the encapsulated value or the result of {@code failureMapper}
	 * @throws NullPointerException if {@code failureMapper} is null
	 * @see #getOrElse(Supplier)
	 * @see #getOrElse(Function)
	 */
	public default T getOrElse(
			Function<? super E, ? extends T> failureMapper) {
		return this.fold(identity(), failureMapper);
	}

	/**
	 * If success, return the encapsulated value.
	 *
	 * If failure, invoke {@code failureSupplier} and return the answer.
	 *
	 * @param failureSupplier supplier used to generate a value
	 * @return the encapsulated value or the result of {@code failureSupplier}
	 * @throws NullPointerException if {@code failureSupplier} is null
	 * @see #getOrElse(Function)
	 * @see #getOrDefault(Object)
	 */
	public default T getOrElse(
			Supplier<? extends T> failureSupplier) {
		requireNonNull(failureSupplier);

		return this.getOrElse(e -> failureSupplier.get());
	}

	/**
	 * If success, return the encapsulated value.
	 *
	 * If failure, return {@code defaultValue}.
	 *
	 * @param defaultValue the default value
	 * @return the encapsulated value or {@code defaultValue}
	 * @see #getOrElse(Supplier)
	 * @see #getOrNull()
	 */
	public default T getOrDefault(T defaultValue) {
		return this.getOrElse(() -> defaultValue);
	}

	/**
	 * If success, return the encapsulated value.
	 *
	 * If failure, return null.
	 *
	 * @return the encapsulated value or null
	 * @see #getOrDefault(Object)
	 */
	public default T getOrNull() {
		return this.getOrDefault(null);
	}

	/**
	 * If success, return the encapsulated value.
	 *
	 * If failure, apply {@code failureMapper} to the encapsulated
	 * error and throw the {@code Throwable} answer.
	 *
	 * @param <X> the class of the {@code Throwable} to be thrown
	 * @param failureMapper transform to apply to the encapsulated error
	 * @return the encapsulated value
	 * @throws X if this is a failure {@code Result}
	 * @throws NullPointerException if {@code failureMapper} is null
	 * @throws NullPointerException if this is a failure Result
	 * 								and {@code failureMapper} returns null
	 * @see #getOrElse(Function)
	 * @see #errorOrNull()
	 */
	public default <X extends Throwable> T getOrThrow(
			Function<? super E, ? extends X> failureMapper) throws X {
		requireNonNull(failureMapper);

		if(this.isFailure()) {
			throw requireNonNull(failureMapper.apply(requireNonNull(this.errorOrNull())));
		}

		return this.getOrNull();
	}

	/**
	 * If success, apply {@code successMapper} to the encapsulated value
	 * and return the answer.
	 *
	 * If failure, apply {@code failureMapper} to the encapsulated error
	 * and return the answer.
	 *
	 * <p>Note that the only material difference between this method
	 * and {@link #fold(Function, Function) fold()} (other than the types)
	 * is that {@code flatMap()} is guaranteed to return a non-null value.
	 *
	 * @param <U> the class of the encapsulated value for the new {@code Result}
	 * @param <X> the class of the encapsulated error for the new {@code Result}
	 * @param successMapper transform to apply to the encapsulated value
	 * @param failureMapper transform to apply to the encapsulated error
	 * @return a new {@code Result}
	 * @throws NullPointerException if {@code successMapper} is null
	 * 								or {@code failureMapper} is null
	 * @throws NullPointerException if this is a success {@code Result}
	 * 								and {@code successMapper} returns null
	 * @throws NullPointerException if this is a failure {@code Result}
	 * 								and {@code failureMapper} returns null
	 * @see #flatMap(Function)
	 * @see #flatMapFailure(Function)
	 * @see #mapBoth(Function, Function)
	 * @see #fold(Function, Function)
	 */
	public default <U, X> Result<U, X> flatMapBoth(
			Function<? super T, ? extends Result<U, X>> successMapper,
			Function<? super E, ? extends Result<U, X>> failureMapper) {
		return this.fold(
			successMapper
				.andThen(Objects::requireNonNull),
			failureMapper
				.andThen(Objects::requireNonNull));
	}

	/**
	 * If success, apply {@code successMapper} to the encapsulated value
	 * and return the answer.
	 *
	 * If failure, return a new {@code Result} containing the original encapsulated
	 * error.
	 *
	 * @param <U> the class of the encapsulated value for the new {@code Result}
	 * @param successMapper transform to apply to the encapsulated value
	 * @return the {@code Result}
	 * @throws NullPointerException if {@code successMapper} is null
	 * @throws NullPointerException if this is a success {@code Result}
	 * 								and {@code successMapper} returns null
	 * @see #flatMapFailure(Function)
	 * @see #flatMapBoth(Function, Function)
	 */
	public default <U> Result<U, E> flatMap(
			Function<? super T, ? extends Result<U, E>> successMapper) {
		return this.flatMapBoth(successMapper, Result::failure);
	}

	/**
	 * If success, return a new {@code Result} containing the original encapsulated
	 * value.
	 *
	 * If failure, apply {@code failureMapper} to the encapsulated error
	 * and return the answer.
	 *
	 * @param <X> the class of the encapsulated error for the new {@code Result}
	 * @param failureMapper transform to apply to the encapsulated error
	 * @return the {@code Result}
	 * @throws NullPointerException if {@code failureMapper} is null
	 * @throws NullPointerException if this is a failure {@code Result}
	 * 								and {@code failureMapper} returns null
	 * @see #flatMap(Function)
	 * @see #flatMapBoth(Function, Function)
	 */
	public default <X> Result<T, X> flatMapFailure(
			Function<? super E, ? extends Result<T, X>> failureMapper) {
		return this.flatMapBoth(Result::success, failureMapper);
	}

	/**
	 * If success, apply {@code successMapper} to the encapsulated value
	 * and return a new success {@code Result} containing the answer.
	 *
	 * If failure, apply {@code failureMapper} to the encapsulated error
	 * and return a new failure {@code Result} containing the answer.
	 *
	 * @param <U> the class of the encapsulated value for the new {@code Result}
	 * @param <X> the class of the encapsulated error for the new {@code Result}
	 * @param successMapper transform to apply to the encapsulated value
	 * @param failureMapper transform to apply to the encapsulated error
	 * @return a new Result
	 * @throws NullPointerException if {@code successMapper} is null
	 * 								or {@code failureMapper} is null
	 * @throws NullPointerException if this is a failure {@code Result}
	 * 								and {@code failureMapper} returns null
	 * @see #map(Function)
	 * @see #mapFailure(Function)
	 * @see #flatMapBoth(Function, Function)
	 * @see #fold(Function, Function)
	 */
	public default <U, X> Result<U, X> mapBoth(
			Function<? super T, ? extends U> successMapper,
			Function<? super E, ? extends X> failureMapper) {
		return this.flatMapBoth(
			successMapper
				.andThen(Result::success),
			failureMapper
				.andThen(Objects::requireNonNull)
				.andThen(Result::failure));
	}

	/**
	 * If success, return a new success {@code Result} containing the original
	 * encapsulated value.
	 *
	 * If failure, apply {@code failureMapper} to the encapsulated error
	 * and return a new failure {@code Result} containing the answer.
	 *
	 * @param <X> the class of the encapsulated error for the new {@code Result}
	 * @param failureMapper transform to apply to the encapsulated error
	 * @return a new {@code Result}
	 * @throws NullPointerException if {@code failureMapper} is null
	 * @see #map(Function)
	 * @see #mapBoth(Function, Function)
	 */
	public default <X> Result<T, X> mapFailure(
			Function<? super E, ? extends X> failureMapper) {
		return this.mapBoth(identity(), failureMapper);
	}

	/**
	 * If success, apply {@code successMapper} to the encapsulated value
	 * and return a new success {@code Result} containing the answer.
	 *
	 * If failure, return a new failure {@code Result} containing the original
	 * encapsulated error.
	 *
	 * @param <U> the class of the encapsulated value for the new {@code Result}
	 * @param successMapper transform to apply to the encapsulated value
	 * @return a new {@code Result}
	 * @throws NullPointerException if {@code successMapper} is null
	 * @see #mapFailure(Function)
	 * @see #mapBoth(Function, Function)
	 */
	public default <U> Result<U, E> map(
			Function<? super T, ? extends U> successMapper) {
		return this.mapBoth(successMapper, identity());
	}

	/**
	 * If success, test {@code predicate} on the encapsulated value and, if
	 * {@code false}, apply {@code successMapper} to the encapsulated value
	 * and return a new failure {@code Result} containing the answer, otherwise
	 * return this {@code Result}.
	 *
	 * If failure, return this {@code Result}.
	 *
	 * @param predicate test to apply to the encapsulated value
	 * @param successMapper transform to apply to the encapsulated value
	 * @return the Result
	 * @throws NullPointerException if {@code predicate} is null
	 * 								or {@code successMapper} is null
	 * @throws NullPointerException if this is a success {@code Result},
	 * 								{@code predicate} tests {@code true},
	 * 								and {@code successMapper} returns null
	 * @see #require(Predicate)
	 * @see #failIf(Predicate, Function)
	 */
	public default Result<T, E> require(
			Predicate<? super T> predicate,
			Function<? super T, ? extends E> successMapper) {
		requireNonNull(predicate);
		requireNonNull(successMapper);

		return this.flatMapBoth(
			t -> predicate.test(t)
				? this
				: failure(requireNonNull(successMapper.apply(t))),
			e -> this);
	}

	/**
	 * If success, test {@code predicate} on the encapsulated value and, if
	 * {@code true}, return a new success {@code Result} containing the original
	 * encapsulated value, otherwise return a new failure {@code Result} containing
	 * a generic error.
	 *
	 * If failure, cast the encapsulated error to {@code Object} and return
	 * a new failure Result containing the answer.
	 *
	 * <p>This variant of {@code require()} is typically used when only
	 * the state of the {@code Result} (e.g. success or failure) is
	 * significant and the specific error is irrelevant.
	 *
	 * <p>Although the encapsulated error {@code Object} is guaranteed
	 * to be non-null, implementers should not make assumptions
	 * about what this {@code Object} actually is.
	 *
	 * @param predicate test to apply to the encapsulated value
	 * @return the {@code Result}
	 * @throws NullPointerException if {@code predicate} is null
	 * @see #require(Predicate, Function)
	 * @see #failIf(Predicate)
	 */
	public default Result<T, Object> require(
			Predicate<? super T> predicate) {
		return this
			.mapFailure(Object.class::cast)
			.require(predicate, t -> new Object());
	}

	/**
	 * If success, test {@code predicate} on the encapsulated value and, if
	 * {@code true}, apply {@code successMapper} to the encapsulated value
	 * and return a new failure {@code Result} containing the answer, otherwise
	 * return this {@code Result}.
	 *
	 * If failure, return this {@code Result}.
	 *
	 * @param predicate test to apply to the encapsulated value
	 * @param successMapper transform to apply to the encapsulated value
	 * @return the {@code Result}
	 * @throws NullPointerException if {@code predicate} is null
	 * 								or {@code successMapper} is null
	 * @see #failIf(Predicate)
	 * @see #require(Predicate, Function)
	 * @see #recoverIf(Predicate, Function)
	 */
	public default Result<T, E> failIf(
			Predicate<? super T> predicate,
			Function<? super T, ? extends E> successMapper) {
		return this.require(predicate.negate(), successMapper);
	}

	/**
	 * If success, test {@code predicate} on the encapsulated value and, if
	 * {@code false}, return a new success {@code Result} containing the original
	 * encapsulated value, otherwise return a new failure {@code Result} containing
	 * a generic error.
	 *
	 * If failure, cast the encapsulated error to {@code Object} and return
	 * a new failure {@code Result} containing the answer.
	 *
	 * <p>This variant of {@code failIf()} is typically used when only
	 * the state of the {@code Result} (e.g. success or failure) is
	 * significant and the specific error is irrelevant.
	 *
	 * <p>Although the encapsulated error {@code Object} is guaranteed
	 * to be non-null, implementers should not make assumptions
	 * about what this {@code Object} actually is.
	 *
	 * @param predicate test to apply to the encapsulated value
	 * @return the {@code Result}
	 * @throws NullPointerException if {@code predicate} is null
	 * @see #failIf(Predicate, Function)
	 * @see #require(Predicate)
	 */
	public default Result<T, Object> failIf(
			Predicate<? super T> predicate) {
		return this.require(predicate.negate());
	}

	/**
	 * If success, apply {@code successMapper} to the encapsulated value
	 * and return a new failure {@code Result} containing the answer.}.
	 *
	 * If failure, return this {@code Result}.
	 *
	 * @param successMapper transform to apply to the encapsulated value
	 * @return the {@code Result}
	 * @throws NullPointerException if {@code successMapper} is null
	 * @see #fail()
	 * @see #failIf(Predicate, Function)
	 * @see #recover(Function)
	 */
	public default Result<T, E> fail(
			Function<? super T, ? extends E> successMapper) {
		return this.failIf(t -> true, successMapper);
	}

	/**
	 * If success, return a new failure {@code Result} containing
	 * a generic error.
	 *
	 * If failure, cast the encapsulated error to {@code Object} and
	 * return a new failure {@code Result} containing the answer.
	 *
	 * <p>This variant of {@code fail()} is typically used when only
	 * the state of the {@code Result} (e.g. success or failure) is
	 * significant and the specific error is irrelevant.
	 *
	 * <p>Although the encapsulated error {@code Object} is guaranteed
	 * to be non-null, implementers should not make assumptions
	 * about what this {@code Object} actually is.
	 *
	 * @return the {@code Result}
	 * @see #fail(Function)
	 * @see #failIf(Predicate)
	 */
	public default Result<T, Object> fail() {
		return this.failIf(t -> true);
	}

	/**
	 * If success, submit the encapsulated value to {@code successAction}
	 * and return this {@code Result}.
	 *
	 * If failure, submit the encapsulated error to {@code failureAction}.
	 * and return this {@code Result}.
	 *
	 * @param successAction {@code Consumer} of the encapsulated value
	 * @param failureAction {@code Consumer} of the encapsulated error
	 * @return this {@code Result}
	 * @throws NullPointerException if {@code successAction} is null
	 * 								or {@code failureAction} is null
	 * @see #onFailure(Consumer)
	 * @see #onSuccess(Consumer)
	 */
	public default Result<T, E> onEither(
			Consumer<? super T> successAction,
			Consumer<? super E> failureAction) {
		requireNonNull(successAction);
		requireNonNull(failureAction);

		return this.flatMapBoth(
			t -> { successAction.accept(t); return this; },
			e -> { failureAction.accept(e); return this; });
	}

	/**
	 * If success, return this {@code Result}.
	 *
	 * If failure, submit the encapsulated error to {@code failureAction}.
	 * and return this {@code Result}.
	 *
	 * @param failureAction {@code Consumer} of the encapsulated error
	 * @return this {@code Result}
	 * @throws NullPointerException if {@code failureAction} is null
	 * @see #onSuccess(Consumer)
	 * @see #onEither(Consumer, Consumer)
	 */
	public default Result<T, E> onFailure(
			Consumer<? super E> failureAction) {
		return this.onEither(t -> { }, failureAction);
	}

	/**
	 * If success, submit the encapsulated value to {@code successAction}
	 * and return this {@code Result}.
	 *
	 * If failure, return this {@code Result}.
	 *
	 * @param successAction {@code Consumer} of the encapsulated value
	 * @return this {@code Result}
	 * @throws NullPointerException if {@code successAction} is null
	 * @see #onFailure(Consumer)
	 * @see #onEither(Consumer, Consumer)
	 */
	public default Result<T, E> onSuccess(
			Consumer<? super T> successAction) {
		return this.onEither(successAction, e -> { });
	}

	/**
	 * If success, return this {@code Result}.
	 *
	 * If failure, test {@code predicate} on the encapsulated error and,
	 * if {@code true}, apply {@code failureMapper} to the encapsulated
	 * error and return a new success {@code Result} containing the answer,
	 * otherwise return this {@code Result}.
	 *
	 * @param predicate test to apply to the encapsulated error
	 * @param failureMapper transform to apply to the encapsulated error
	 * @return the {@code Result}
	 * @throws NullPointerException if {@code predicate} is null
	 * 								or {@code failureMapper} is null
	 * @see #recover(Function)
	 * @see #failIf(Predicate, Function)
	 */
	public default Result<T, E> recoverIf(
			Predicate<? super E> predicate,
			Function<? super E, ? extends T> failureMapper) {
		requireNonNull(predicate);
		requireNonNull(failureMapper);

		return this.flatMapBoth(
			t -> this,
			e -> predicate.test(e)
				? success(failureMapper.apply(e))
				: this);
	}

	/**
	 * If success, return this {@code Result}.
	 *
	 * If failure, apply {@code failureMapper} to the encapsulated
	 * error and return a new success {@code Result} containing the answer.
	 *
	 * @param failureMapper transform to apply to the encapsulated error
	 * @return the {@code Result}
	 * @throws NullPointerException if {@code failureMapper} is null
	 * @see #recoverIf(Predicate, Function)
	 * @see #fail(Function)
	 */
	public default Result<T, E> recover(
			Function<? super E, ? extends T> failureMapper) {
		return this.recoverIf(e -> true, failureMapper);
	}

	/**
	 * Create a new failure {@code Result} for the given error.
	 *
	 * @param <U> the class of the encapsulated value
	 * @param <X> the class of the encapsulated error
	 * @param error the error for the {@code Result}
	 * @return a failure {@code Result}
	 * @throws NullPointerException if {@code error} is null
	 * @see #success(Object)
	 * @see FailureResult
	 */
	public static <U, X> Result<U, X> failure(X error) {
		return new FailureResult<>(error);
	}

	/**
	 * Create a new success {@code Result} for the given value,
	 * which could be null.
	 *
	 * @param <U> the class of the encapsulated value
	 * @param <X> the class of the encapsulated error
	 * @param value the nullable value for the {@code Result}
	 * @return a success {@code Result}
	 * @see #failure(Object)
	 * @see SuccessResult
	 */
	public static <U, X> Result<U, X> success(U value) {
		return new SuccessResult<>(value);
	}

	/**
	 * An abstract implementation of {@code Result}.
	 *
	 * <p>This exists primarily as a convenience implementation
	 * of the {@code toString()}, {@code hashCode()}, and {@code equals()} methods
	 * since they cannot be defined as default methods
	 * in the enclosing interface.
	 *
	 * @param <U> the class of the encapsulated value
	 * @param <X> the class of the encapsulated error
	 * @see SuccessResult
	 * @see FailureResult
	 */
	public static abstract class AbstractResult<U, X>
			implements Result<U, X> {

		/**
		 * If success, get a String representation of the encapsulated
		 * value and return a String {@code Success(v)} where
		 * v is the answer.
		 *
		 * If failure, get a String representation of the encapsulated
		 * error and return a String {@code Failure(e)} where
		 * e is the answer.
		 */
		@Override
		public String toString() {
			return this.fold(
				t -> String.format("Success(%s)", t),
				e -> String.format("Failure(%s)", e));
		}

		/**
		 * If success, return the hash code of the encapsulated value.
		 *
		 * If failure, return the hash code of the encapsulated error.
		 *
		 * @return the hash code of the encapsulated value or error
		 */
		@Override
		public int hashCode() {
			return this.fold(Objects::hashCode, Objects::hashCode);
		}

		/**
		 * Test if another {@code Object} is equal to this {@code Result}.
		 *
		 * <p>A {@code Result} is considered <b>not</b> equal to {@code this}
		 * if any of the following are true:
		 *
		 * <ul>
		 * 	<li>It is null</li>
		*   <li>It is not a {@code Result}</li>
		*   <li>{@code isFailure()} does not return the same value as {@code this}</li>
		*   <li>{@code isSuccess()} does not return the same value as {@code this}</li>
		*   <li>When success, {@code getOrNull()} does not return the same value as {@code this}, according to {@code equals()}</li>
		*	<li>When failure, {@code errorOrNull()} does not return the same value as {@code this}, according to {@code equals()}</li>
		 * </ul>
		 *
		 * @param obj the object to test for equality with {@code this}
		 * @return {@code true} if {@code obj} is equal to {@code this}
		 */
		@Override
		public boolean equals(Object obj) {
			return success(obj)
				.map(o -> this == o)
				.require(Boolean::booleanValue)
				.recover(b -> this.doEquals(obj))
				.getOrThrow(e -> new IllegalStateException());
		}

		private boolean doEquals(Object obj) {
			return success(obj)
				.require(Result.class::isInstance)
				.map(Result.class::cast)
				.require(o -> this.isFailure() == o.isFailure())
				.require(o -> this.isSuccess() == o.isSuccess())
				.require(o -> this.fold(
					t -> Objects.equals(t, o.getOrNull()),
					e -> Objects.equals(e, o.errorOrNull())))
				.isSuccess();
		}

	}

	/**
	 * An implementation of a {@code Result} that represents failure.
	 *
	 * <p>A {@code FailureResult} encapsulates a single, non-null error
	 * {@code Object} of class {@code X}.
	 *
	 * @param <U> the class of the encapsulated value
	 * @param <X> the class of the encapsulated error
	 * @see Result#failure(Object)
	 * @see SuccessResult
	 */
	public static final class FailureResult<U, X>
			extends AbstractResult<U, X> {

		/**
		 * The error for this {@code FailureResult}.
		 */
		private final X error;

		/**
		 * Create a new {@code FailureResult} for the given error.
		 *
		 * @param error the error for the {@code FailureResult}
		 * @throws NullPointerException if {@code error} is null
		 * @see Result#failure(Object)
		 */
		private FailureResult(X error) {
			super();

			this.error = requireNonNull(error);
		}

		@Override
		public <R> R fold(
				Function<? super U, ? extends R> successMapper,
				Function<? super X, ? extends R> failureMapper) {
			requireNonNull(successMapper);

			return failureMapper.apply(this.error);
		}

	}

	/**
	 * An implementation of a {@code Result} that represents success.
	 *
	 * <p>A {@code SuccessResult} encapsulates a single, possibly
	 * null value of class U.
	 *
	 * @param <U> the class of the encapsulated value
	 * @param <X> the class of the encapsulated error
	 * @see Result#success(Object)
	 * @see FailureResult
	 */
	public static final class SuccessResult<U, X>
			extends AbstractResult<U, X> {

		/**
		 * The (possibly null) value of this {@code SuccessResult}.
		 */
		private final U value;

		/**
		 * Create a new {@code SuccessResult} for the given {@code value},
		 * which could be null.
		 *
		 * @param value the value for the {@code SuccessResult}
		 * @see Result#success(Object)
		 */
		private SuccessResult(U value) {
			super();

			this.value = value;
		}

		@Override
		public <R> R fold(
				Function<? super U, ? extends R> successMapper,
				Function<? super X, ? extends R> failureMapper) {
			requireNonNull(failureMapper);

			return successMapper.apply(this.value);
		}

	}

}
