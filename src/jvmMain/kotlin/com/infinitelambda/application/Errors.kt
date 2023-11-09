package com.infinitelambda.application

import arrow.core.raise.Raise

sealed interface DomainError

typealias FormResultErrors = Raise<FormResultError>

sealed interface FormResultError: DomainError
data class InvalidFormResult(val message: String): FormResultError
data class FormResultNotPersisted(val message: String, val cause: Throwable): FormResultError