package net.royalur.builder;

import net.royalur.Game;
import net.royalur.model.*;
import net.royalur.model.identity.AnonymousPlayer;
import net.royalur.model.path.AsebPathPair;
import net.royalur.model.path.BellPathPair;
import net.royalur.model.shape.AsebBoardShape;
import net.royalur.model.shape.StandardBoardShape;
import net.royalur.rules.Dice;
import net.royalur.rules.standard.*;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A builder to help in the creation of custom games of the Royal Game of Ur.
 */
public class GameBuilder {

    /**
     * A builder for constructing games that follow the standard rules.
     * @param <R> The type of rolls that the dice produce.
     * @param <SELF> The type of the subclass of this builder.
     */
    public static abstract class BaseStandardGameBuilder<R extends Roll, SELF extends BaseStandardGameBuilder<R, SELF>> {

        /**
         * The identity of the light player.
         */
        protected final @Nonnull PlayerIdentity lightIdentity;

        /**
         * The identity of the dark player.
         */
        protected final @Nonnull PlayerIdentity darkIdentity;

        /**
         * The shape of the board.
         */
        protected final @Nonnull BoardShape boardShape;

        /**
         * The paths that each player must take around the board.
         */
        protected final @Nonnull PathPair paths;

        /**
         * The number of pieces that each player starts with.
         */
        protected final int startingPieceCount;

        /**
         * Instantiates a builder to build games following a standard rule set.
         * @param lightIdentity The identity of the light player.
         * @param darkIdentity The identity of the dark player.
         * @param boardShape The shape of the board.
         * @param paths The paths that each player must take around the board.
         * @param startingPieceCount The number of pieces that each player starts with.
         */
        public BaseStandardGameBuilder(
                @Nonnull PlayerIdentity lightIdentity,
                @Nonnull PlayerIdentity darkIdentity,
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                int startingPieceCount
        ) {
            this.lightIdentity = lightIdentity;
            this.darkIdentity = darkIdentity;
            this.boardShape = boardShape;
            this.paths = paths;
            this.startingPieceCount = startingPieceCount;
        }

        /**
         * Copies this game builder with new settings.
         * @param lightIdentity The identity of the light player.
         * @param darkIdentity The identity of the dark player.
         * @param boardShape The shape of the board.
         * @param paths The paths that each player must take around the board.
         * @param startingPieceCount The number of pieces that each player starts with.
         * @return A copy of this game builder with updated settings.
         */
        protected abstract @Nonnull SELF copy(
                @Nonnull PlayerIdentity lightIdentity,
                @Nonnull PlayerIdentity darkIdentity,
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                int startingPieceCount
        );

        /**
         * Copies this game builder with the shape of the board set to {@code boardType}.
         * @param boardType The type of board shape to use for generated games.
         * @return A copy of this game builder with the shape of the board set to {@code boardType}.
         */
        public @Nonnull SELF boardShape(@Nonnull BoardType boardType) {
            return boardShape(boardType.create());
        }

        /**
         * Copies this game builder with the shape of the board set to {@code boardShape}.
         * @param boardShape The shape of the board to use for generated games.
         * @return A copy of this game builder with the shape of the board set to {@code boardShape}.
         */
        public @Nonnull SELF boardShape(@Nonnull BoardShape boardShape) {
            return copy(lightIdentity, darkIdentity, boardShape, paths, startingPieceCount);
        }

        /**
         * Copies this game builder with the paths taken by each player set to {@code pathType}.
         * @param pathType The type of paths to be taken by each player.
         * @return A copy of this game builder with the paths taken by each player set to {@code pathType}.
         */
        public @Nonnull SELF paths(@Nonnull PathType pathType) {
            return paths(pathType.create());
        }

        /**
         * Copies this game builder with the paths taken by each player set to {@code paths}.
         * @param paths The paths to be taken by each player.
         * @return A copy of this game builder with the paths taken by each player set to {@code paths}.
         */
        public @Nonnull SELF paths(@Nonnull PathPair paths) {
            return copy(lightIdentity, darkIdentity, boardShape, paths, startingPieceCount);
        }

        /**
         * Copies this game builder with the player identities set to {@code lightIdentity} and {@code darkIdentity}.
         * @param lightIdentity The identity of the light player.
         * @param darkIdentity The identity of the dark player.
         * @return A copy of this game builder with the player identities
         *         set to {@code lightIdentity} and {@code darkIdentity}.
         */
        public @Nonnull SELF players(@Nonnull PlayerIdentity lightIdentity, @Nonnull PlayerIdentity darkIdentity) {
            return copy(lightIdentity, darkIdentity, boardShape, paths, startingPieceCount);
        }

        /**
         * Copies this game builder with the dice to be used to generate dice rolls set to {@code dice}.
         * @param dice The dice to be used to generate dice rolls.
         * @param <NR> The type of the dice rolls generated by {@code dice}.
         * @return A copy of this game builder with the dice to be used to generate dice rolls set to {@code dice}.
         */
        public abstract <NR extends Roll> @Nonnull BaseStandardGameBuilder<NR, ?> dice(@Nonnull Dice<NR> dice);

        /**
         * Copies this game builder with the number of starting pieces of each player
         * set to {@code startingPieceCount}.
         * @param startingPieceCount The number of pieces that each player starts with in the game.
         * @return A copy of this game builder with the number of starting pieces of each player
         *         set to {@code startingPieceCount}.
         */
        public @Nonnull SELF startingPieceCount(int startingPieceCount) {
            return copy(lightIdentity, darkIdentity, boardShape, paths, startingPieceCount);
        }

        /**
         * Generates a standard rule set to match the settings in this builder.
         * The rules do not contain any player identification information set in this builder.
         * @return A standard rule set to match the settings in this builder.
         */
        public abstract @Nonnull StandardRuleSet<StandardPiece, PlayerState, R> buildRules();

        /**
         * Builds a new game using the rules set in this builder.
         * @return A new game using the rules set in this builder.
         */
        public abstract @Nonnull Game<StandardPiece, PlayerState, R> build();
    }

    /**
     * A builder for constructing games that follow the standard rules, with dice
     * that produce standard rolls of type {@link Roll}.
     */
    public static class StandardGameBuilder extends BaseStandardGameBuilder<Roll, StandardGameBuilder> {

        /**
         * Instantiates a builder to build games following a standard rule set.
         * @param lightIdentity The identity of the light player.
         * @param darkIdentity The identity of the dark player.
         * @param boardShape         The shape of the board.
         * @param paths              The paths that each player must take around the board.
         * @param startingPieceCount The number of pieces that each player starts with.
         */
        public StandardGameBuilder(
                @Nonnull PlayerIdentity lightIdentity,
                @Nonnull PlayerIdentity darkIdentity,
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                int startingPieceCount
        ) {
            super(lightIdentity, darkIdentity, boardShape, paths, startingPieceCount);
        }

        @Override
        protected @Nonnull StandardGameBuilder copy(
                @Nonnull PlayerIdentity lightIdentity,
                @Nonnull PlayerIdentity darkIdentity,
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                int startingPieceCount
        ) {
            return new StandardGameBuilder(lightIdentity, darkIdentity, boardShape, paths, startingPieceCount);
        }

        @Override
        public <R extends Roll> @Nonnull StandardWithDiceGameBuilder<R> dice(@Nonnull Dice<R> dice) {
            return new StandardWithDiceGameBuilder<>(
                    lightIdentity, darkIdentity, boardShape, paths, dice, startingPieceCount
            );
        }

        @Override
        public @Nonnull StandardRuleSet<StandardPiece, PlayerState, Roll> buildRules() {
            return new StandardRuleSet<>(
                    boardShape,
                    paths,
                    new StandardDice(),
                    new StandardPieceProvider(),
                    new StandardPlayerStateProvider(startingPieceCount)
            );
        }

        @Override
        public @Nonnull StandardGame build() {
            StandardRuleSet<StandardPiece, PlayerState, Roll> rules = buildRules();
            return new StandardGame(
                    rules, lightIdentity, darkIdentity,
                    List.of(rules.generateInitialGameState())
            );
        }
    }

    /**
     * A builder for constructing games that follow the standard rules, with dice
     * that produce non-standard types of rolls.
     * @param <R> The type of rolls that the dice produce.
     */
    public static class StandardWithDiceGameBuilder<R extends Roll>
            extends BaseStandardGameBuilder<R, StandardWithDiceGameBuilder<R>> {

        /**
         * The dice that is used to generate rolls.
         */
        private final @Nonnull Dice<R> dice;

        /**
         * Instantiates a builder to build games following a standard rule set.
         * @param lightIdentity The identity of the light player.
         * @param darkIdentity The identity of the dark player.
         * @param boardShape         The shape of the board.
         * @param paths              The paths that each player must take around the board.
         * @param dice               The dice that is used to generate rolls.
         * @param startingPieceCount The number of pieces that each player starts with.
         */
        public StandardWithDiceGameBuilder(
                @Nonnull PlayerIdentity lightIdentity,
                @Nonnull PlayerIdentity darkIdentity,
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                @Nonnull Dice<R> dice,
                int startingPieceCount
        ) {
            super(lightIdentity, darkIdentity, boardShape, paths, startingPieceCount);
            this.dice = dice;
        }

        /**
         * Copies this game builder with new settings.
         * @param lightIdentity The identity of the light player.
         * @param darkIdentity The identity of the dark player.
         * @param boardShape The shape of the board.
         * @param paths The paths that each player must take around the board.
         * @param dice The dice that is used to generate rolls.
         * @param startingPieceCount The number of pieces that each player starts with.
         * @param <NR> The type of the dice rolls generated by {@code dice}.
         * @return A copy of this game builder with updated settings.
         */
        protected <NR extends Roll> @Nonnull StandardWithDiceGameBuilder<NR> copy(
                @Nonnull PlayerIdentity lightIdentity,
                @Nonnull PlayerIdentity darkIdentity,
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                @Nonnull Dice<NR> dice,
                int startingPieceCount
        ) {
            return new StandardWithDiceGameBuilder<>(
                    lightIdentity, darkIdentity, boardShape, paths, dice, startingPieceCount
            );
        }

        @Override
        protected @Nonnull StandardWithDiceGameBuilder<R> copy(
                @Nonnull PlayerIdentity lightIdentity,
                @Nonnull PlayerIdentity darkIdentity,
                @Nonnull BoardShape boardShape,
                @Nonnull PathPair paths,
                int startingPieceCount
        ) {
            return copy(lightIdentity, darkIdentity, boardShape, paths, dice, startingPieceCount);
        }

        @Override
        public <NR extends Roll> @Nonnull StandardWithDiceGameBuilder<NR> dice(@Nonnull Dice<NR> dice) {
            return copy(lightIdentity, darkIdentity, boardShape, paths, dice, startingPieceCount);
        }

        @Override
        public @Nonnull StandardRuleSet<StandardPiece, PlayerState, R> buildRules() {
            return new StandardRuleSet<>(
                    boardShape,
                    paths,
                    dice,
                    new StandardPieceProvider(),
                    new StandardPlayerStateProvider(startingPieceCount)
            );
        }

        @Override
        public @Nonnull Game<StandardPiece, PlayerState, R> build() {
            StandardRuleSet<StandardPiece, PlayerState, R> rules = buildRules();
            return new Game<>(
                    rules, lightIdentity, darkIdentity,
                    List.of(rules.generateInitialGameState())
            );
        }
    }

    /**
     * Instantiate a builder to help in the creation of custom games of the Royal Game of Ur.
     */
    public GameBuilder() {}

    /**
     * Creates a new builder that allows the construction of games following the standard rules
     * from RoyalUr.net.
     * @return A new builder that allows the construction of games following the standard rules.
     */
    public @Nonnull StandardGameBuilder standard() {
        return new StandardGameBuilder(
                new AnonymousPlayer(),
                new AnonymousPlayer(),
                new StandardBoardShape(),
                new BellPathPair(),
                7
        );
    }

    /**
     * Creates a new builder that allows the construction of games following the Aseb rules.
     * @return A new builder that allows the construction of games following the Aseb rules.
     */
    public @Nonnull StandardGameBuilder aseb() {
        return new StandardGameBuilder(
                new AnonymousPlayer(),
                new AnonymousPlayer(),
                new AsebBoardShape(),
                new AsebPathPair(),
                5
        );
    }
}
