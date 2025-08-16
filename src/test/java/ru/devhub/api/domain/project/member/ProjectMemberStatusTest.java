package ru.devhub.api.domain.project.member;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ProjectMemberStatusTest {

    static Stream<Arguments> validCases() {
        return Stream.of(
                arguments("owner",   ProjectMemberStatus.OWNER),
                arguments(" Owner ", ProjectMemberStatus.OWNER),
                arguments("ACTIVE",  ProjectMemberStatus.ACTIVE),
                arguments(" active", ProjectMemberStatus.ACTIVE),
                arguments("invited", ProjectMemberStatus.INVITED),
                arguments(" left ",  ProjectMemberStatus.LEFT),
                arguments("removed", ProjectMemberStatus.REMOVED)
        );
    }

    @ParameterizedTest
    @MethodSource("validCases")
    void fromString_parses_case_insensitive_and_trims(String input, ProjectMemberStatus expected) {
        assertEquals(expected, ProjectMemberStatus.fromString(input));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n", "unknown", "act ive", "owner1", "0"})
    void fromString_throws_on_invalid_or_blank(String input) {
        assertThrows(IllegalArgumentException.class, () -> ProjectMemberStatus.fromString(input));
    }

    @Test
    void fromString_throws_on_weird_input() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> ProjectMemberStatus.fromString("  \t \n ")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> ProjectMemberStatus.fromString("NOT_A_STATUS")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> ProjectMemberStatus.fromString(null))
        );
    }

}
