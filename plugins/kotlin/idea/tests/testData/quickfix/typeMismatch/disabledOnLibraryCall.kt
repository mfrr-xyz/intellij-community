// "Change parameter 'transformer' of function 'map' to 'Int'" "false"
// K2_AFTER_ERROR: Argument type mismatch: actual type is 'String', but 'Function1<Int, R (of fun <T, R> Iterable<T>.map)>' was expected.
// K2_AFTER_ERROR: Cannot infer type for this parameter. Specify it explicitly.
// ERROR: Unresolved reference: listOf

fun foo(transformer: String) {
    listOf(1, 2, 3).map(tra<caret>nsformer)
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.ChangeParameterTypeFix
// FUS_K2_QUICKFIX_NAME: org.jetbrains.kotlin.idea.k2.refactoring.changeSignature.quickFix.ChangeParameterTypeFix