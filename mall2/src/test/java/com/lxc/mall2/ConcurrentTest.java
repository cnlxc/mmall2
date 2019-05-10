package com.lxc.mall2;

public class ConcurrentTest {
    public TreeNode reConstructBinaryTree(int [] pre,int [] in) {
        return reConstruct(pre,0,pre.length-1,in,0,in.length-1);
    }


    private TreeNode reConstruct(int[] pre,int preStart,int preEnd,int[] in,int inStart,int inEnd){
        if(preStart > preEnd || inStart > inEnd){
            return null;
        }
        TreeNode root = new TreeNode(pre[preStart] );
        for(int i=inStart;i<inEnd;i++){
            if(pre[preStart] == in[i]){
                root.left = reConstruct(pre,preStart+1,preStart+i-inStart,in,inStart,i-1);
                root.right = reConstruct(pre,preStart+i-inStart+1,preEnd,in,i+1,inEnd);
                break;
            }

        }
        return root;

    }
     class TreeNode {
             int val;
             TreeNode left;
             TreeNode right;
             TreeNode(int x) { val = x; }
        }
}
