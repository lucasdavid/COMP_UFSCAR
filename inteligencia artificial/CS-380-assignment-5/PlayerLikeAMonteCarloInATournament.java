package Assignment_5;import java.util.LinkedList;import java.util.List;import java.util.logging.Level;import java.util.logging.Logger;final class PlayerLikeAMonteCarloInATournament extends OthelloPlayer{OthelloStateNode root;boolean maximize=true;long initial,time=1000000000;List<Thread>threads;public PlayerLikeAMonteCarloInATournament(){threads=new LinkedList<>();}class OthelloStateNode{LinkedList<OthelloStateNode>children;OthelloStateNode parent;OthelloMove movement;OthelloState state;float average;int visits;public OthelloStateNode(){children=null;state=null;parent=null;movement=null;average=0;visits=0;}public OthelloStateNode(OthelloState _state){this();state=_state;}public void backup(int _average){visits++;average=(float)(visits-1)/visits*average+1f/visits*_average;if(parent!=null)parent.backup(_average);}public List<OthelloStateNode>generateChildren(){if(children==null){children=new LinkedList<>();for(OthelloMove move:state.generateMoves()){OthelloStateNode child=new OthelloStateNode(state.applyMoveCloning(move));child.movement=move;child.parent=this;children.add(child);}}return children;}public OthelloStateNode getRandomChild(){return generateChildren().get((int)(Math.random()*generateChildren().size()));}public OthelloStateNode bestChild(){OthelloStateNode best=generateChildren().get(0);for(OthelloStateNode current:children)if(current.average>best.average)best=current;return best;}public OthelloStateNode worseChild(){OthelloStateNode worse=generateChildren().get(0);for(OthelloStateNode current:children)if(current.average<worse.average)worse=current;return worse;}}public PlayerLikeAMonteCarloInATournament time(long _time){time=_time*1000000;return this;}@Override public OthelloMove getMove(OthelloState _state){maximize=_state.nextPlayerToMove==OthelloState.PLAYER1;return MonteCarloTreeSearch(_state);}OthelloMove MonteCarloTreeSearch(OthelloState _state){initial=System.nanoTime();root=new OthelloStateNode(_state);if(root.generateChildren().isEmpty())return null;for(int i=0;i<Runtime.getRuntime().availableProcessors();i++){Thread current=new Thread(new Runnable(){@Override public void run(){while(System.nanoTime()-initial<time){OthelloStateNode current=treePolicy(root,maximize);current.backup(defaultPolicy(current));}}});threads.add(current);current.start();}for(Thread thread:threads){try{thread.join();}catch(InterruptedException ex){Logger.getLogger(PlayerLikeAMonteCarloInATournament.class.getName()).log(Level.SEVERE,null,ex);}}threads.clear();return maximize?root.bestChild().movement:root.worseChild().movement;}synchronized OthelloStateNode treePolicy(OthelloStateNode _node,boolean _maximize){if(_node.generateChildren().isEmpty())return _node;OthelloStateNode child=_node.children.removeFirst();if(child.visits==0){_node.children.add(child);return child;}else _node.children.add(0,child);if(Math.random()<=.1)return treePolicy(_node.getRandomChild(),!_maximize);return treePolicy(_maximize?_node.bestChild():_node.worseChild(),!_maximize);}int defaultPolicy(OthelloStateNode _node){OthelloState state=_node.state.clone();List<OthelloMove>moves;while(!(moves=state.generateMoves()).isEmpty())state.applyMove(moves.get((int)(Math.random()*moves.size())));return state.score();}}