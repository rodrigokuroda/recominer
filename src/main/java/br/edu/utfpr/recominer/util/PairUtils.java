package br.edu.utfpr.recominer.util;

import br.edu.utfpr.recominer.metric.network.UserPairDirectionalWeighted;
import br.edu.utfpr.recominer.model.Commenter;
import br.edu.utfpr.recominer.model.UserToUserEdgeByCommentDirectional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Rodrigo T. Kuroda
 */
public class PairUtils {

    /**
     *
     * @param commenters the commenters of pair file (comment on issue that pair
     * file has committed)
     * @return pairedCommenters the commenters paired
     */
    public static Map<UserToUserEdgeByCommentDirectional, UserToUserEdgeByCommentDirectional> pairCommenters(List<Commenter> commenters) {
        Map<UserToUserEdgeByCommentDirectional, UserToUserEdgeByCommentDirectional> pairCommenter = new HashMap<>();
        for (int i = 0; i < commenters.size(); i++) {
            Commenter author1 = commenters.get(i);
            for (int j = i - 1; j >= 0; j--) {
                Commenter author2 = commenters.get(j);
                if (!author1.equals(author2)) {
                    UserToUserEdgeByCommentDirectional pair = new UserToUserEdgeByCommentDirectional(author1.getName(), author1.getEmail(), author2.getName(), author2.getEmail());
                    if (pairCommenter.containsKey(pair)) {
                        pairCommenter.get(pair).inc();
                    } else {
                        pairCommenter.put(pair, pair);
                    }
                }
            }
        }
        return pairCommenter;
    }

    /**
     *
     * @param commenters the commenters of pair file (comment on issue that pair
     * file has committed)
     * @return pairedCommenters the commenters paired
     */
    public static Map<UserPairDirectionalWeighted, UserPairDirectionalWeighted> buildUserPairDirectionalWeighted(List<Commenter> commenters) {
        Map<UserPairDirectionalWeighted, UserPairDirectionalWeighted> pairCommenter = new HashMap<>();
        for (int i = 0; i < commenters.size(); i++) {
            Commenter author1 = commenters.get(i);
            for (int j = i - 1; j >= 0; j--) {
                Commenter author2 = commenters.get(j);
                if (!author1.equals(author2)) {
                    UserPairDirectionalWeighted pair = new UserPairDirectionalWeighted(author1.getName(), author1.getEmail(), author2.getName(), author2.getEmail());
                    if (pairCommenter.containsKey(pair)) {
                        pairCommenter.get(pair).inc();
                    } else {
                        pairCommenter.put(pair, pair);
                    }
                }
            }
        }
        return pairCommenter;
    }

}
