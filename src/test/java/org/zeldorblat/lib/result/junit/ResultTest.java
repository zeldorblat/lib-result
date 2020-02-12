package org.zeldorblat.lib.result.junit;

import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.opentest4j.AssertionFailedError;
import org.zeldorblat.lib.result.Result;

public class ResultTest {

	@Test
	public void testFailure() {
		Object expected = new Object();

		Result<Object, Object> actual = Result.failure(expected);
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertFalse(actual.isSuccess());
		assertSame(expected, actual.errorOrNull());
		assertNull(actual.getOrNull());
	}

	@Test
	public void testFailureWithNullError() {
		assertThrowsNpe(() -> Result.failure(null));
	}

	@Test
	public void testSuccess() {
		Object expected = new Object();

		Result<Object, Object> actual = Result.success(expected);
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertFalse(actual.isFailure());
		assertSame(expected, actual.getOrNull());
		assertNull(actual.errorOrNull());
	}

	@Test
	public void testSuccessWithNullValue() {
		Result<Object, Object> actual = Result.success(null);
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertFalse(actual.isFailure());
		assertNull(actual.getOrNull());
		assertNull(actual.errorOrNull());
	}

	@Test
	public void testErrorOrNullWhenFailure() {
		Object expected = new Object();

		assertSame(expected, getFailure(expected).errorOrNull());
	}

	@Test
	public void testErrorOrNullWhenSuccess() {
		assertNull(getSuccess().errorOrNull());
	}

	@Test
	public void testGetOrNullWhenFailure() {
		assertNull(getFailure().getOrNull());
	}

	@Test
	public void testGetOrNullWhenSuccess() {
		Object expected = new Object();

		assertSame(expected, getSuccess(expected).getOrNull());
	}

	@Test
	public void testFoldWhenFailure() {
		Object expected = new Object();

		assertSame(expected, getFailure(expected).fold(t -> fail(), identity()));
	}

	@Test
	public void testFoldWhenSuccess() {
		Object expected = new Object();

		assertSame(expected, getSuccess(expected).fold(identity(), e -> fail()));
	}

	@Test
	public void testFoldWhenFailureWithNullSuccessMapper() {
		assertThrowsNpe(() -> getFailure().fold(null, e -> fail()));
	}

	@Test
	public void testFoldWhenSuccessWithNullSuccessMapper() {
		assertThrowsNpe(() -> getSuccess().fold(null, e -> fail()));
	}

	@Test
	public void testFoldWhenFailureWithNullFailureMapper() {
		assertThrowsNpe(() -> getFailure().fold(t -> fail(), null));
	}

	@Test
	public void testFoldWhenSuccessWithNullFailureMapper() {
		assertThrowsNpe(() -> getSuccess().fold(t -> fail(), null));
	}

	@Test
	public void testGetOrDefaultWhenFailure() {
		Object expected = new Object();

		assertSame(expected, getFailure().getOrDefault(expected));
	}

	@Test
	public void testGetOrDefaultWhenSuccess() {
		Object expected = new Object();

		assertSame(expected, getSuccess(expected).getOrDefault(new Object()));
	}

	@Test
	public void testGetOrDefaultWhenNullValue() {
		assertNull(getSuccess(null).getOrDefault(new Object()));
	}

	@Test
	public void testGetOrElseWhenFailure() {
		Object expected = new Object();

		assertSame(expected, getFailure(expected).getOrElse(identity()));
	}

	@Test
	public void testGetOrElseWhenFailureAndReturningNull() {
		assertNull(getFailure().getOrElse(e -> null));
	}

	@Test
	public void testGetOrElseWhenFailureWithNullFailureMapper() {
		Function<Object, Object> mapper = null;

		assertThrowsNpe(() -> getFailure().getOrElse(mapper));
	}

	@Test
	public void testGetOrElseWhenSuccess() {
		Object expected = new Object();

		assertSame(expected, getSuccess(expected).getOrElse(e -> fail()));
	}

	@Test
	public void testGetOrElseWhenSuccessWithNullValue() {
		assertNull(getSuccess(null).getOrElse(e -> fail()));
	}

	@Test
	public void testGetOrElseWhenSuccessWithNullFailureMapper() {
		Function<Object, Object> mapper = null;

		assertThrowsNpe(() -> getSuccess().getOrElse(mapper));
	}

	@Test
	public void testGetOrElseGetWhenFailure() {
		Object expected = new Object();

		assertSame(expected, getFailure().getOrElse(() -> expected));
	}

	@Test
	public void testGetOrElseGetWhenFailureAndReturningNull() {
		assertNull(getFailure().getOrElse(() -> null));
	}

	@Test
	public void testGetOrElseGetWhenFailureWithNullFailureSupplier() {
		Supplier<Object> supplier = null;

		assertThrowsNpe(() -> getFailure().getOrElse(supplier));
	}

	@Test
	public void testGetOrElseGetWhenSuccess() {
		Object expected = new Object();

		assertSame(expected, getSuccess(expected).getOrElse(() -> fail()));
	}

	@Test
	public void testGetOrElseGetWhenSuccessWithNullValue() {
		assertNull(getSuccess(null).getOrElse(() -> fail()));
	}

	@Test
	public void testGetOrElseGetWhenSuccessWithNullFailureSupplier() {
		Supplier<Object> supplier = null;

		assertThrowsNpe(() -> getSuccess().getOrElse(supplier));
	}

	@Test
	public void testGetOrThrowWhenFailure() {
		Exception expected = new Exception();

		assertSame(
			expected,
			assertThrows(Exception.class, () -> getFailure(expected).getOrThrow(identity())));
	}

	@Test
	public void testGetOrThrowWhenSuccess() throws Exception {
		Object expected = new Object();

		assertSame(expected, getSuccess(expected).getOrThrow(e -> fail()));
	}

	@Test
	public void testGetOrThrowWhenFailureAndMapperNull() {
		assertThrowsNpe(() -> getFailure().getOrThrow(null));
	}

	@Test
	public void testGetOrThrowWhenSuccessAndMapperNull() throws Exception {
		assertThrowsNpe(() -> getSuccess().getOrThrow(null));
	}

	@Test
	public void testGetOrThrowWhenFailureAndMapperReturnsNull() {
		assertThrowsNpe(() -> getFailure().getOrThrow(e -> null));
	}

	@Test
	public void testMapWhenFailure() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).map(t -> fail());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testMapWhenFailureWithNullTransform() {
		assertThrowsNpe(() -> getFailure().map(null));
	}

	@Test
	public void testMapWhenSuccess() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).map(identity());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual.getOrNull());
	}

	@Test
	public void testMapWhenSuccessWithNullTransform() {
		assertThrowsNpe(() -> getSuccess().map(null));
	}

	@Test
	public void testMapFailureWhenFailure() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).mapFailure(identity());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testMapFailureWhenFailureWithNullTransform() {
		assertThrowsNpe(() -> getFailure().mapFailure(null));
	}

	@Test
	public void testMapFailureWhenFailureWithNullResult() {
		assertThrowsNpe(() -> getFailure().mapFailure(e -> null));
	}

	@Test
	public void testMapFailureWhenSuccess() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).mapFailure(e -> fail());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual.getOrNull());
	}

	@Test
	public void testMapFailureWhenSuccessWithNullTransform() {
		assertThrowsNpe(() -> getSuccess().mapFailure(null));
	}

	@Test
	public void testFailWhenFailure() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).fail(t -> fail());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testFailWhenSuccess() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).fail(identity());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertEquals(expected, actual.errorOrNull());
	}

	@Test
	public void testFailWhenSuccessTransformReturnsNull() {
		assertThrowsNpe(() -> getSuccess().fail(t -> null));
	}

	@Test
	public void testFailWhenFailureAndTransformNull() {
		assertThrowsNpe(() -> getFailure().fail(null));
	}

	@Test
	public void testFailWhenSuccessAndTransformNull() {
		assertThrowsNpe(() -> getSuccess().fail(null));
	}

	@Test
	public void testFailGenericWhenFailure() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).fail();
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testFailGenericWhenSuccess() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).fail();
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertNotSame(expected, actual.errorOrNull());
	}

	@Test
	public void testFailIfWhenFailureAndPredicateTrue() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).failIf(t -> true, t -> fail());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testFailIfWhenSuccessAndPredicateTrue() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).failIf(t -> true, identity());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertEquals(expected, actual.errorOrNull());
	}

	@Test
	public void testFailIfWhenSuccessAndPredicateTrueTransformReturnsNull() {
		assertThrowsNpe(() -> getSuccess().failIf(t -> true, t -> null));
	}

	@Test
	public void testFailIfWhenFailureAndPredicateTrueAndTransformNull() {
		assertThrowsNpe(() -> getFailure().failIf(t -> true, null));
	}

	@Test
	public void testFailIfWhenSuccessAndPredicateTrueAndTransformNull() {
		assertThrowsNpe(() -> getSuccess().failIf(t -> true, null));
	}

	@Test
	public void testFailIfWhenFailureAndPredicateFalse() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).failIf(t -> false, t -> fail());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testFailIfWhenSuccessAndPredicateFalse() {
		Result<Object, Object> expected = getSuccess();

		Result<Object, Object> actual = expected.failIf(t -> false, t -> fail());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual);
	}

	@Test
	public void testFailIfWhenSuccessAndPredicateFalseTransformReturnsNull() {
		Result<Object, Object> expected = getSuccess();

		Result<Object, Object> actual = expected.failIf(t -> false, t -> null);
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual);
	}

	@Test
	public void testFailIfWhenFailureAndPredicateFalseAndTransformNull() {
		assertThrowsNpe(() -> getFailure().failIf(t -> false, null));
	}

	@Test
	public void testFailIfWhenSuccessAndPredicateFalseAndTransformNull() {
		assertThrowsNpe(() -> getSuccess().failIf(t -> false, null));
	}

	@Test
	public void testFailIfWhenFailureAndPredicateNull() {
		assertThrowsNpe(() -> getFailure().failIf(null, t -> fail()));
	}

	@Test
	public void testFailIfWhenSuccessAndPredicateNull() {
		assertThrowsNpe(() -> getSuccess().failIf(null, t -> fail()));
	}

	@Test
	public void testFailIfGenericWhenFailureAndPredicateTrue() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).failIf(t -> true);
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testFailIfGenericWhenSuccessAndPredicateTrue() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).failIf(t -> true);
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertNotSame(expected, actual.errorOrNull());
	}

	@Test
	public void testFailIfGenericWhenFailureAndPredicateFalse() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).failIf(t -> false);
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testFailIfGenericWhenSuccessAndPredicateFalse() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).failIf(t -> false);
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual.getOrNull());
	}

	@Test
	public void testFailIfGenericWhenFailureAndPredicateNull() {
		assertThrowsNpe(() -> getFailure().failIf(null));
	}

	@Test
	public void testFailIfGenericWhenSuccessAndPredicateNull() {
		assertThrowsNpe(() -> getSuccess().failIf(null));
	}

	@Test
	public void testRequireWhenFailureAndPredicateFalse() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).require(t -> false, t -> fail());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testRequireWhenSuccessAndPredicateFalse() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).require(t -> false, identity());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testRequireWhenSuccessAndPredicateFalseTransformReturnsNull() {
		assertThrowsNpe(() -> getSuccess().require(t -> false, t -> null));
	}

	@Test
	public void testRequireWhenFailureAndPredicateFalseAndTransformNull() {
		assertThrowsNpe(() -> getFailure().require(t -> false, null));
	}

	@Test
	public void testRequireWhenSuccessAndPredicateFalseAndTransformNull() {
		assertThrowsNpe(() -> getSuccess().require(t -> false, null));
	}

	@Test
	public void testRequireWhenFailureAndPredicateTrue() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).require(t -> true, t -> fail());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testRequireWhenSuccessAndPredicateTrue() {
		Result<Object, Object> expected = getSuccess();

		Result<Object, Object> actual = expected.require(t -> true, t -> fail());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual);
	}

	@Test
	public void testRequireWhenSuccessAndPredicateTrueTransformReturnsNull() {
		Result<Object, Object> expected = getSuccess();

		Result<Object, Object> actual = expected.require(t -> true, t -> null);
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual);
	}

	@Test
	public void testRequireWhenFailureAndPredicateTrueAndTransformNull() {
		assertThrowsNpe(() -> getFailure().require(t -> true, null));
	}

	@Test
	public void testRequireWhenSuccessAndPredicateTrueAndTransformNull() {
		assertThrowsNpe(() -> getSuccess().require(t -> true, null));
	}

	@Test
	public void testRequireWhenFailureAndPredicateNull() {
		assertThrowsNpe(() -> getFailure().require(null, t -> fail()));
	}

	@Test
	public void testRequireWhenSuccessAndPredicateNull() {
		assertThrowsNpe(() -> getSuccess().require(null, t -> fail()));
	}

	@Test
	public void testRequireGenericWhenFailureAndPredicateFalse() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).require(t -> false);
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testRequireGenericWhenSuccessAndPredicateFalse() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).require(t -> false);
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertNotSame(expected, actual.errorOrNull());
	}

	@Test
	public void testRequireGenericWhenFailureAndPredicateTrue() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).require(t -> true);
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testRequireGenericWhenSuccessAndPredicateTrue() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).require(t -> true);
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual.getOrNull());
	}

	@Test
	public void testRequireGenericWhenFailureAndPredicateNull() {
		assertThrowsNpe(() -> getFailure().require(null));
	}

	@Test
	public void testRequireGenericWhenSuccessAndPredicateNull() {
		assertThrowsNpe(() -> getSuccess().require(null));
	}

	@Test
	public void testFlatMapBothWhenFailure() {
		Object expectedValue = new Object();
		Result<Object, Object> expected = getFailure(expectedValue);

		Result<Object, Object> actual = expected.flatMapBoth(t -> fail(), Result::failure);
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertNotSame(expected, actual);
		assertSame(expectedValue, actual.errorOrNull());
	}

	@Test
	public void testFlatMapBothWhenFailureWithNullSuccessTransform() {
		assertThrowsNpe(() -> getFailure().flatMapBoth(null, t -> fail()));
	}

	@Test
	public void testFlatMapBothWhenFailureWithNullFailureTransform() {
		assertThrowsNpe(() -> getFailure().flatMapBoth(t -> fail(), null));
	}

	@Test
	public void testFlatMapBothWhenFailureWithNullResult() {
		assertThrowsNpe(() -> getFailure().flatMapBoth(t -> fail(), e -> null));
	}

	@Test
	public void testFlatMapBothWhenSuccess() {
		Object expectedValue = new Object();
		Result<Object, Object> expected = getSuccess(expectedValue);

		Result<Object, Object> actual = expected.flatMapBoth(Result::success, e -> fail());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertNotSame(expected, actual);
		assertSame(expectedValue, actual.getOrNull());
	}

	@Test
	public void testFlatMapBothWhenSuccessWithNullSuccessTransform() {
		assertThrowsNpe(() -> getSuccess().flatMapBoth(null, e -> fail()));
	}

	@Test
	public void testFlatMapBothWhenSuccessWithNullFailureTransform() {
		assertThrowsNpe(() -> getSuccess().flatMapBoth(t -> fail(), null));
	}

	@Test
	public void testFlatMapBothWhenSuccessWithNullResult() {
		assertThrowsNpe(() -> getSuccess().flatMapBoth(s -> null, e -> fail()));
	}

	@Test
	public void testFlatMapWhenFailure() {
		Object expectedValue = new Object();
		Result<Object, Object> expected = getFailure(expectedValue);

		Result<Object, Object> actual = expected.flatMap(t -> fail());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertNotSame(expected, actual);
		assertSame(expectedValue, actual.errorOrNull());
	}

	@Test
	public void testFlatMapWhenFailureWithNullSuccessTransform() {
		assertThrowsNpe(() -> getFailure().flatMap(null));
	}

	@Test
	public void testFlatMapWhenSuccess() {
		Object expectedValue = new Object();
		Result<Object, Object> expected = getSuccess(expectedValue);

		Result<Object, Object> actual = expected.flatMap(Result::failure);
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertNotSame(expected, actual);
		assertSame(expectedValue, actual.errorOrNull());
	}

	@Test
	public void testFlatMapWhenSuccessWithNullSuccessTransform() {
		assertThrowsNpe(() -> getSuccess().flatMap(null));
	}

	@Test
	public void testFlatMapWhenSuccessWithNullResult() {
		assertThrowsNpe(() -> getSuccess().flatMap(t -> null));
	}

	@Test
	public void testFlatMapFailureWhenFailure() {
		Object expectedValue = new Object();
		Result<Object, Object> expected = getFailure(expectedValue);

		Result<Object, Object> actual = expected.flatMapFailure(Result::success);
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertNotSame(expected, actual);
		assertSame(expectedValue, actual.getOrNull());
	}

	@Test
	public void testFlatMapFailureWhenFailureWithNullFailureTransform() {
		assertThrowsNpe(() -> getFailure().flatMapFailure(null));
	}

	@Test
	public void testFlatMapFailureWhenFailureWithNullResult() {
		assertThrowsNpe(() -> getFailure().flatMapFailure(e -> null));
	}

	@Test
	public void testFlatMapFailureWhenSuccess() {
		Object expectedValue = new Object();
		Result<Object, Object> expected = getSuccess(expectedValue);

		Result<Object, Object> actual = expected.flatMapFailure(e -> fail());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertNotSame(expected, actual);
		assertSame(expectedValue, actual.getOrNull());
	}

	@Test
	public void testFlatMapFailureWhenSuccessWithNullFailureTransform() {
		assertThrowsNpe(() -> getSuccess().flatMapFailure(null));
	}

	@Test
	public void testMapBothWhenFailure() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).mapBoth(t -> fail(), identity());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual.errorOrNull());
	}

	@Test
	public void testMapBothWhenFailureWithNullSuccessTransform() {
		assertThrowsNpe(() -> getFailure().mapBoth(null, e -> fail()));
	}

	@Test
	public void testMapBothWhenFailureWithNullFailureTransform() {
		assertThrowsNpe(() -> getFailure().mapBoth(t -> fail(), null));
	}

	@Test
	public void testMapBothWhenFailureWithNullResult() {
		assertThrowsNpe(() -> getFailure().mapBoth(t -> fail(), e -> null));
	}

	@Test
	public void testMapBothWhenSuccess() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).mapBoth(identity(), e -> fail());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual.getOrNull());
	}

	@Test
	public void testMapBothWhenSuccessWithNullSuccessTransform() {
		assertThrowsNpe(() -> getSuccess().mapBoth(null, e -> fail()));
	}

	@Test
	public void testMapBothWhenSuccessWithNullFailureTransform() {
		assertThrowsNpe(() -> getSuccess().mapBoth(t -> fail(), null));
	}

	@Test
	public void testMapBothWhenSuccessWithNullResult() {
		Result<Object, Object> actual = getSuccess().mapBoth(t -> null, e -> fail());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertNull(actual.getOrNull());
	}

	@Test
	public void testOnFailureWhenFailure() {
		Integer expectedValue = Integer.valueOf(19);
		Result<Object, Integer> expected = getFailure(expectedValue);

		AtomicInteger c = new AtomicInteger(23);

		Result<Object, Integer> actual = expected.onFailure(c::getAndAdd);
		assertNotNull(actual);

		assertSame(expected, actual);
		assertEquals(42, c.intValue());
	}

	@Test
	public void testOnFailureWhenFailureWithNullConsumer() {
		assertThrowsNpe(() -> getFailure().onFailure(null));
	}

	@Test
	public void testOnFailureWhenSuccess() {
		Result<Object, Object> expected = getSuccess();

		Result<Object, Object> actual = expected.onFailure(e -> fail());
		assertNotNull(actual);

		assertSame(expected, actual);
	}

	@Test
	public void testOnFailureWhenSuccessWithNullConsumer() {
		assertThrowsNpe(() -> getSuccess().onFailure(null));
	}

	@Test
	public void testOnSuccessWhenFailure() {
		Result<Object, Object> expected = getFailure();

		Result<Object, Object> actual = expected.onSuccess(t -> fail());
		assertNotNull(actual);

		assertSame(expected, actual);
	}

	@Test
	public void testOnSuccessWhenFailureWithNullConsumer() {
		assertThrowsNpe(() -> getFailure().onSuccess(null));
	}

	@Test
	public void testOnSuccessWhenSuccess() {
		Integer expectedValue = Integer.valueOf(19);
		Result<Integer, Object> expected = getSuccess(expectedValue);

		AtomicInteger c = new AtomicInteger(23);

		Result<Integer, Object> actual = expected.onSuccess(c::getAndAdd);
		assertNotNull(actual);

		assertSame(expected, actual);
		assertEquals(42, c.intValue());
	}

	@Test
	public void testOnSuccessWhenSuccessWithNullConsumer() {
		assertThrowsNpe(() -> getSuccess().onSuccess(null));
	}

	@Test
	public void testOnEitherWhenFailure() {
		Integer expectedValue = Integer.valueOf(19);
		Result<Object, Integer> expected = getFailure(expectedValue);

		AtomicInteger c = new AtomicInteger(23);

		Result<Object, Integer> actual = expected.onEither(t -> fail(), c::getAndAdd);
		assertNotNull(actual);

		assertSame(expected, actual);
		assertEquals(42, c.intValue());
	}

	@Test
	public void testOnEitherWhenFailureWithNullSuccessConsumer() {
		assertThrowsNpe(() -> getFailure().onEither(null, e -> fail()));
	}

	@Test
	public void testOnEitherWhenFailureWithNullFailureConsumer() {
		assertThrowsNpe(() -> getFailure().onEither(t -> fail(), null));
	}

	@Test
	public void testOnEitherWhenSuccess() {
		Integer expectedValue = Integer.valueOf(19);
		Result<Integer, Object> expected = getSuccess(expectedValue);

		AtomicInteger c = new AtomicInteger(23);

		Result<Integer, Object> actual = expected.onEither(c::getAndAdd, e -> fail());
		assertNotNull(actual);

		assertSame(expected, actual);
		assertEquals(42, c.intValue());
	}

	@Test
	public void testOnEitherWhenSuccessWithNullSuccessConsumer() {
		assertThrowsNpe(() -> getSuccess().onEither(null, e -> fail()));
	}

	@Test
	public void testOnEitherWhenSuccessWithNullFailureConsumer() {
		assertThrowsNpe(() -> getSuccess().onEither(t -> fail(), null));
	}

	@Test
	public void testRecoverWhenFailure() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).recover(identity());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual.getOrNull());
	}

	@Test
	public void testRecoverWhenFailureWithNullTransform() {
		assertThrowsNpe(() -> getFailure().recover(null));
	}

	@Test
	public void testRecoverWhenFailureWithNullResult() {
		Result<Object, Object> actual = getFailure().recover(e -> null);

		assertTrue(actual.isSuccess());
		assertNull(actual.getOrNull());
	}

	@Test
	public void testRecoverWhenSuccess() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).recover(e -> fail());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual.getOrNull());
	}

	@Test
	public void testRecoverWhenSuccessWithNullTransform() {
		assertThrowsNpe(() -> getSuccess().recover(null));
	}

	@Test
	public void testRecoverIfWhenFailureAndPredicateTrue() {
		Object expected = new Object();

		Result<Object, Object> actual = getFailure(expected).recoverIf(e -> true, identity());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual.getOrNull());
	}

	@Test
	public void testRecoverIfWhenFailureWithNullTransformAndPredicateTrue() {
		assertThrowsNpe(() -> getFailure().recoverIf(e -> true, null));
	}

	@Test
	public void testRecoverIfWhenFailureWithNullResultAndPredicateTrue() {
		Result<Object, Object> actual = getFailure().recoverIf(e -> true, e -> null);
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertNull(actual.getOrNull());
	}

	@Test
	public void testRecoverIfWhenSuccessAndPredicateTrue() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).recoverIf(e -> true, e -> fail());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual.getOrNull());
	}

	@Test
	public void testRecoverIfWhenSuccessWithNullTransformAndPredicateTrue() {
		assertThrowsNpe(() -> getSuccess().recoverIf(e -> true, null));
	}

	@Test
	public void testRecoverIfWhenFailureAndPredicateFalse() {
		Result<Object, Object> expected = getFailure();

		Result<Object, Object> actual = expected.recoverIf(e -> false, e -> fail());
		assertNotNull(actual);

		assertTrue(actual.isFailure());
		assertSame(expected, actual);
	}

	@Test
	public void testRecoverIfWhenFailureWithNullTransformAndPredicateFalse() {
		assertThrowsNpe(() -> getFailure().recoverIf(e -> false, null));
	}

	@Test
	public void testRecoverIfWhenSuccessAndPredicateFalse() {
		Object expected = new Object();

		Result<Object, Object> actual = getSuccess(expected).recoverIf(e -> false, e -> fail());
		assertNotNull(actual);

		assertTrue(actual.isSuccess());
		assertSame(expected, actual.getOrNull());
	}

	@Test
	public void testRecoverIfWhenSuccessWithNullTransformAndPredicateFalse() {
		assertThrowsNpe(() -> getSuccess().recoverIf(e -> false, null));
	}

	@Test
	public void testRecoverIfWhenFailureAndPredicateNull() {
		assertThrowsNpe(() -> getFailure().recoverIf(null, e -> fail()));
	}

	@Test
	public void testRecoverIfWhenSuccessAndPredicateNull() {
		assertThrowsNpe(() -> getSuccess().recoverIf(null, e -> fail()));
	}

	@Test
	public void testToStringWhenFailure() {
		assertEquals("Failure(foo)", getFailure(new Object() {
			@Override
			public String toString() {
				return "foo";
			}
		}).toString());
	}

	@Test
	public void testToStringWhenSuccess() {
		assertEquals("Success(foo)", getSuccess(new Object() {
			@Override
			public String toString() {
				return "foo";
			}
		}).toString());
	}

	@Test
	public void testToStringWhenSuccessWithNullValue() {
		assertEquals("Success(null)", getSuccess(null).toString());
	}

	@Test
	public void testHashCodeWhenFailure() {
		Object expected = new Object();

		assertEquals(Objects.hashCode(expected), getFailure(expected).hashCode());
	}

	@Test
	public void testHashCodeWhenSuccess() {
		Object expected = new Object();

		assertEquals(Objects.hashCode(expected), getSuccess(expected).hashCode());
	}

	@Test
	public void testHashCodeWhenSuccessWithNullValue() {
		assertEquals(Objects.hashCode(null), getSuccess(null).hashCode());
	}

	@Test
	public void testEqualsWhenFailureWhenEqual() {
		Object expected = new Object();

		Result<Object, Object> actual1 = getFailure(expected);
		Result<Object, Object> actual2 = getFailure(expected);

		assertNotSame(actual1, actual2);
		assertTrue(actual1.equals(actual2));
	}

	@Test
	public void testEqualsWhenFailureWhenNotEqual() {
		Result<Object, Object> actual1 = getFailure(new Object());
		Result<Object, Object> actual2 = getFailure(new Object());

		assertNotSame(actual1, actual2);
		assertFalse(actual1.equals(actual2));
	}

	@Test
	public void testEqualsWhenSuccessWhenEqual() {
		Object expected = new Object();

		Result<Object, Object> actual1 = getSuccess(expected);
		Result<Object, Object> actual2 = getSuccess(expected);

		assertNotSame(actual1, actual2);
		assertTrue(actual1.equals(actual2));
	}

	@Test
	public void testEqualsWhenSuccessWhenNotEqual() {
		Result<Object, Object> actual1 = getSuccess(new Object());
		Result<Object, Object> actual2 = getSuccess(new Object());

		assertNotSame(actual1, actual2);
		assertFalse(actual1.equals(actual2));
	}

	@Test
	public void testEqualsWhenSuccessWhenOtherValueNull() {
		Result<Object, Object> actual1 = getSuccess(new Object());
		Result<Object, Object> actual2 = getSuccess(null);

		assertNotSame(actual1, actual2);
		assertFalse(actual1.equals(actual2));
	}

	@Test
	public void testEqualsWhenSuccessWhenBothValuesNull() {
		Result<Object, Object> actual1 = getSuccess(null);
		Result<Object, Object> actual2 = getSuccess(null);

		assertNotSame(actual1, actual2);
		assertTrue(actual1.equals(actual2));
	}

	@Test
	public void testEqualsWhenSuccessWithSameResult() {
		Result<Object, Object> actual = getSuccess();

		assertTrue(actual.equals(actual));
	}

	@Test
	public void testEqualsWhenFailureWithSameResult() {
		Result<Object, Object> actual = getFailure();

		assertTrue(actual.equals(actual));
	}

	@Test
	public void testEqualsWhenSuccessWhenOtherNull() {
		assertFalse(getSuccess().equals(null));
	}

	@Test
	public void testEqualsWhenFailureWhenOtherNull() {
		assertFalse(getFailure().equals(null));
	}

	@Test
	public void testEqualsWhenFailureWhenOtherSuccess() {
		assertFalse(getFailure().equals(getSuccess()));
	}

	@Test
	public void testEqualsWhenSuccessWhenOtherFailure() {
		assertFalse(getSuccess().equals(getFailure()));
	}

	@Test
	public void testAssertThrowsNpeWhenNullPointerException() {
		NullPointerException expected = new NullPointerException();

		NullPointerException actual = assertThrowsNpe(() -> { throw expected; });
		assertNotNull(actual);

		assertSame(expected, actual);
	}

	@Test
	public void testAssertThrowsNpeWhenNoException() {
		assertThrows(AssertionFailedError.class, () -> assertThrowsNpe(() -> { }));
	}

	@Test
	public void testAssertThrowsNpeWhenExceptionNotNullPointerException() {
		assertThrows(AssertionFailedError.class, () -> assertThrowsNpe(() -> { throw new Exception(); }));
	}

	private static Result<Object, Object> getSuccess() {
		return getSuccess(new Object());
	}

	private static Result<Object, Object> getFailure() {
		return getFailure(new Object());
	}

	private static <T> Result<T, Object> getSuccess(T value) {
		Result<T, Object> result = Result.success(value);
		assertNotNull(result);
		return result;
	}

	private static <E> Result<Object, E> getFailure(E error) {
		Result<Object, E> result = Result.failure(error);
		assertNotNull(result);
		return result;
	}

	private static NullPointerException assertThrowsNpe(Executable executable) {
		return assertThrows(NullPointerException.class, executable);
	}

	private static <T> T fail() {
		return org.junit.jupiter.api.Assertions.fail(new IllegalStateException());
	}

}
