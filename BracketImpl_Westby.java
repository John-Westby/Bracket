package tournament;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BracketImpl_Westby<P> extends BracketAbstract<P>
{
    public BracketImpl_Westby(List<P> participantMatchups) {
        super(participantMatchups);
    }

    @Override
    public int getMaxLevel() {
        int participantCount = predictions.size() / 2;
        int maxLevel = (int) (Math.log(participantCount) / Math.log(2));
        return maxLevel + 1; // +1 to account for levels starting at 1
    }

    @Override
    public Set<Set<P>> getGroupings(int level) {
        Set<Set<P>> groupings = new HashSet<>();
        int totalParticipants = predictions.size();
        int groupSize = (int) Math.pow(2, level); // Groups of 2^level participants

        // Calculate the starting index for the groupings based on the level
        // Level 1: indexes 0-1, Level 2: indexes 2-3, etc.
        // We need to adjust the index based on how many participants there are at each level.
        int startIndex = (int) Math.pow(2, level) - 1; // Starting index for this level

        for (int i = startIndex; i < totalParticipants; i += groupSize) {
            Set<P> participantGroup = new HashSet<>();
            
            // Group participants in pairs
            for (int j = 0; j < groupSize && (i + j) < totalParticipants; j++) {
                P participant = predictions.get(i + j);
                if (participant != null) {
                    participantGroup.add(participant);
                }
            }
            
            if (participantGroup.size() > 1) { // Ensure we have at least 2 participants
                groupings.add(participantGroup);
            }
        }
        
        return groupings;
    }
    
    
    @Override
    public Set<P> getViableParticipants(Set<P> participants) {
        Set<P> viableParticipants = new HashSet<>();

        // Iterate through the participants in the provided set
        for (P participant : participants) {
            // Check if the participant has not lost any match
            if (participant != null && predictions.contains(participant)) {
                int index = getParticipantIndex(participant);
                if (index != -1) {
                    // Check if the participant is still in the bracket (has not been eliminated)
                    if (hasNotLost(index)) {
                        viableParticipants.add(participant);
                    }
                }
            }
        }

        return viableParticipants;
    }

    // Helper method to check if a participant has not lost
    private boolean hasNotLost(int index) {
        // In a typical tournament structure, if a participant has not reached the final
        // and is still present in the predictions list, they have not lost.
        // This assumes that any participant at index < predictions.size() has not lost.
        return index < predictions.size();
    }




    @Override
    public void setWinCount(P participant, int winCount) {
        if (participant == null || !predictions.contains(participant)) {
            throw new AssertionError("Participant not in tournament or null.");
        }

        // Simulate setting win count logic
        int index = getParticipantIndex(participant);
        if (index == -1) {
            throw new IllegalArgumentException("Participant not found: " + participant);
        }

        // Recursively update parents
        while (winCount > 0 && index > 0) {
            int parentIndex = getParentIndex(index);
            predictions.set(parentIndex, participant);
            index = parentIndex;
            winCount--;
        }
    }

    private int getParticipantIndex(P participant) {
        return predictions.indexOf(participant);
    }

    private int getParentIndex(int index) {
        if (index == 0) {
            return -1; // Top of the bracket has no parent
        }
        return (index - 1) / 2; // Parent is floor((index - 1) / 2)
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null || getClass() != obj.getClass()) {
//            return false;
//        }
//
//        BracketImpl_Westby<P> other = (BracketImpl_Westby<P>) obj;
//        if (this.getMaxLevel() != other.getMaxLevel()) {
//            return false;
//        }
//
//        for (int level = 0; level <= this.getMaxLevel(); level++) {
//            Set<Set<P>> thisGroupings = this.getGroupings(level);
//            Set<Set<P>> otherGroupings = other.getGroupings(level);
//
//            if (!thisGroupings.equals(otherGroupings)) {
//                return false;
//            }
//        }
//        return true;
//    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        BracketImpl_Westby<P> other = (BracketImpl_Westby<P>) obj;
        if (this.getMaxLevel() != other.getMaxLevel()) {
            return false;
        }

        for (int level = 0; level <= this.getMaxLevel(); level++) {
            Set<Set<P>> thisGroupings = this.getGroupings(level);
            Set<Set<P>> otherGroupings = other.getGroupings(level);

            // Check if sizes are different
            if (thisGroupings.size() != otherGroupings.size()) {
                System.out.println("Size mismatch at level " + level);
                System.out.println("thisGroupings.size(): " + thisGroupings.size());
                System.out.println("otherGroupings.size(): " + otherGroupings.size());
                return false;
            }

            if (!thisGroupings.equals(otherGroupings)) {
                System.out.println("Groupings mismatch at level " + level);
                System.out.println("thisGroupings: " + thisGroupings);
                System.out.println("otherGroupings: " + otherGroupings);
                return false;
            }
        }
        return true;
    }
    
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		List<P> bracketList = new ArrayList<>();
		int maxLevel = this.getMaxLevel();
		
		for (int level = maxLevel; level >= 0; level--) {
			Set<Set<P>> groupings = this.getGroupings(level);
			for (Set<P> grouping : groupings) {
				for (P participant : grouping) {
					if (participant == null) {
						bracketList.add((P) "*");
					} else {
						bracketList.add(participant);
					}
					
				}
			}
		}
		for (int i = 0; i < bracketList.size(); i++) {
			sb.append(bracketList.get(i));
			if (i < bracketList.size() - 1) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

}
	
