package common.toolkit.util;

import static common.toolkit.constant.BaseConstant.WORD_SEPARATOR;
import static common.toolkit.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.constant.SvnConstant.SVN_BRANCHES;
import static common.toolkit.constant.SvnConstant.SVN_TRUNK;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.toolkit.constant.RegExpConstant;
import common.toolkit.util.io.FileUtil;


/**
 * 类说明: SVN相关工具类
 * @author 银时 yinshi.nc@taobao.com
 */
public class SvnUtil {

	
	static Pattern unifiedDiffChunkRe = Pattern.compile( "^@@\\s+-(?:(\\d+)(?:,(\\d+))?)\\s+\\+(?:(\\d+)(?:,(\\d+))?)\\s+@@$" );
	
	
	
	
	/**
	 * parse the path of file in project from it's svn path.
	 * 
	 * @return e.g.
	 *         reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga
	 *         -example-web/src/main/webapp/WEB-INF/sadfsadfsqlmapadsaf.xml
	 * @throws Exception
	 */
	public static String parseFileProjectPathFromSvnPath( String svnPath ) throws Exception {
		if ( StringUtil.isBlank( svnPath ) ) {
			return EMPTY_STRING;
		}

		int index = -1;

		int indexOfTrunk = svnPath.indexOf( SVN_TRUNK );
		int indexOfBranches = svnPath.indexOf( SVN_BRANCHES );

		// 必须找到第一个trunk或branches, 文件路径自有的关键字不能算.
		if ( -1 == indexOfTrunk && -1 != indexOfBranches ) {
			index = indexOfBranches + SVN_BRANCHES.length() + 1;
		} else if ( -1 == indexOfBranches && -1 != indexOfTrunk ) {
			index = indexOfTrunk + SVN_TRUNK.length() + 1;
		} else if ( -1 == indexOfBranches && -1 == indexOfTrunk ) {
			throw new Exception( svnPath + " is a illegal svn path" );
		} else if ( -1 != indexOfBranches && -1 != indexOfTrunk ) {
			if ( indexOfBranches < indexOfTrunk ) {
				index = indexOfBranches + SVN_BRANCHES.length() + 1;
			} else if ( indexOfBranches > indexOfTrunk ) {
				index = indexOfTrunk + SVN_TRUNK.length() + 1;
			}
		}
		return svnPath.substring( index );
	}

	/**
	 * parse the path of file in project from it's svn path with svn prefix
	 * 
	 * @return e.g.
	 *         trunk/reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3
	 *         -ga-example-web/src/main/webapp/WEB-INF/asdfsqlmapadsaf.xml
	 * @throws Exception
	 */
	public static String parseFileProjectPathFromSvnPathWithSvnPrefix( String svnPath ) throws Exception {
		if ( StringUtil.isBlank( svnPath ) ) {
			return EMPTY_STRING;
		}

		int index = -1;

		int indexOfTrunk = svnPath.indexOf( SVN_TRUNK );
		int indexOfBranches = svnPath.indexOf( SVN_BRANCHES );

		// 必须找到第一个trunk或branches, 文件路径自有的关键字不能算.
		if ( -1 == indexOfTrunk && -1 != indexOfBranches ) {
			index = indexOfBranches;
		} else if ( -1 == indexOfBranches && -1 != indexOfTrunk ) {
			index = indexOfTrunk;
		} else if ( -1 == indexOfBranches && -1 == indexOfTrunk ) {
			throw new Exception( svnPath + " is a illegal svn path" );
		} else if ( -1 != indexOfBranches && -1 != indexOfTrunk ) {
			if ( indexOfBranches < indexOfTrunk ) {
				index = indexOfBranches;
			} else if ( indexOfBranches > indexOfTrunk ) {
				index = indexOfTrunk;
			}
		}
		return svnPath.substring( index );
	}
	
	
	
	
	/**
     * 将一个diff文本按照不同文件进行拆解
     * @param diffContentAll 	      待拆解的内容
     * @param pathList 		                完整文件名
     * @return Map<String,String>  <文件名, diff内容>
	 * @throws Exception 
     */
    public static Map<String,String> splitDiff(String diffContentAll, List<String> pathList) throws Exception{
        if(diffContentAll == null || diffContentAll.isEmpty() 
                || pathList == null || pathList.isEmpty()){
           return null;
        }
        Map<String,String> targetMap = new LinkedHashMap<String,String>();
        if( pathList.size() == 1 ){
            targetMap.put(pathList.get(0), diffContentAll);
            return targetMap;
        }
        
        //将所有文件分隔行分隔成一个易于区分的分隔符
        String tempString = diffContentAll.replaceAll( RegExpConstant.REGEX_OF_DIFF_INDEX, WORD_SEPARATOR );
        
        String[] diffSplit = tempString.split( WORD_SEPARATOR );
        
        // i = 1 是因为第一个是多余的且空.
        for( int i = 1; i < diffSplit.length; i++ ){
        	
        	String diffContent = EMPTY_STRING;
        	String key         = EMPTY_STRING;
			try {
				diffContent = diffSplit[i];
			} catch ( Exception e ) {
				throw new Exception( "数组越界: 从diffSplit获取数据, length=" + diffSplit.length + " 但是i= " + i  );
			}
        	try {
				key = pathList.get( i - 1 );
			} catch ( Exception e ) {
				throw new Exception( "List越界: 从pathList获取数据, size=" + pathList.size() + " 但是i= " + ( i-1 )  );
			}
        	
        	targetMap.put( key, diffContent ); 
        }
        
        return targetMap;
    }

	
	
	/**
	 * 拼装DIFF内容到新文件
	 * @param   originalFileName	原文件的完整文件名
	 * @param   diffFileName		Diff文件的完整文件名
	 * @return	String              拼装后的完整内容
	 */
	public static String patchDiffFile( String originalFileName, String diffFileName ) throws Exception{
		
		List<String> original = FileUtil.fileToListByLine( originalFileName );
		List<String> patched =  FileUtil.fileToListByLine( diffFileName );

		return patchDiff( original, patched );
	}
	
	
	
	
	/**
	 * 拼装DIFF内容到新文件
	 * @param   originalFileContent	原文件的内容 
	 * @param   diffFileContent		Diff文件的内容
	 * @return	String              拼装后的完整内容
	 */
	public static String patchDiffContent( String originalFileContent, String diffFileContent ) throws Exception{
		
		List<String> original = FileUtil.stringToListByLine( originalFileContent );
		List<String> patched =  FileUtil.stringToListByLine( diffFileContent );

		return patchDiff( original, patched );
	}
	
	
	/**
	 * 拼装DIFF内容到新文件
	 * @param   originalFileLineList	原文件按行的内容集合
	 * @param   diffFileLineList		Diff文件按行的内容集合
	 * @return	String                  拼装后的完整内容
	 */
	private static String patchDiff( List<String> originalFileLineList, List<String> diffFileLineList ){
		
		Map<Integer, String> mapInsert = new LinkedHashMap<Integer, String>();
		Map<Integer, String> mapDelete = new LinkedHashMap<Integer, String>();

		int    index = 0;
		boolean flag = false;

		for ( String line : diffFileLineList ) {

			Matcher matcherOfRevision = unifiedDiffChunkRe.matcher( line );

			/** 解析出初始行号 */
			if ( matcherOfRevision.find() ) {
				String str = matcherOfRevision.group();
				str = str.replaceAll( "@@", "" ).trim().split( "," )[0].replace( "-", "" );
				index = Integer.parseInt( str );
				flag = true;
				continue;
			}
			
			if( flag ){
				// 如果是要从原来文件中删除的
				if ( line.startsWith( "-" ) ) {
					continue;
				} else if ( line.startsWith( "+" ) ) {
					mapInsert.put( index, line );
					index++;
				}else{
					index++;
				}
			}
		}
		
		index = 0;
		flag  = false;
		for ( String line : diffFileLineList ) {

			Matcher matcherOfRevision = unifiedDiffChunkRe.matcher( line );

			/** 解析出初始行号 */
			if ( matcherOfRevision.find() ) {
				String str = matcherOfRevision.group();
				str = str.replaceAll( "@@", "" ).trim().split( "," )[0].replace( "-", "" );
				index = Integer.parseInt( str );
				flag = true;
				continue;
			}
			
			if( flag ){
				// 如果是要从原来文件中删除的
				if ( line.startsWith( "+" ) ) {
					continue;
				} else if ( line.startsWith( "-" ) ) {
					mapDelete.put( index, line );
					index++;
				}else{
					index++;
				}
			}
		}

		//现在已经获得了两个可以用的ADD和DELETE的map了.
		List<String> tempFile = new ArrayList<String>();
		List<String> newFile  = new ArrayList<String>();
		int lineIndex = 0;
		
		//先处理mapDelete
		for( String line : originalFileLineList ){
			lineIndex ++;
			String lineContentDelete = mapDelete.get( lineIndex );

			if( null ==  lineContentDelete ){
				//此行没有变动
				tempFile.add( line );
			}
			
		}
		
		//现在处理mapInsert
		lineIndex = 0;
		for( String line : tempFile ){
			lineIndex ++;
			String lineContentInsert = mapInsert.get( lineIndex );

			if( null ==  lineContentInsert ){
				newFile.add( line );
			}else{
				
				boolean isOk = true;
				while( isOk ){
					newFile.add( lineContentInsert.replaceFirst( "\\+", "" ) );
					mapInsert.put( lineIndex, null );
					lineIndex ++;
					lineContentInsert = mapInsert.get( lineIndex ); 
					
					if( null == lineContentInsert )
						isOk = false;
				}
				
				newFile.add( line );
				
			}
			
		}
		
		
		
		//接下去要把mapInsert中多余的加入newFile中去.
		for( int _index : mapInsert.keySet() ){
			String lineContent = mapInsert.get( _index );
			if( null == lineContent )
				continue;
			
			newFile.add( lineContent.replaceFirst( "\\+", "" ) );
		}
		
		StringBuilder newContent = new StringBuilder();
		if( "\n".equalsIgnoreCase( newFile.get( newFile.size()-1 ) ) ){
			newFile.remove( newFile.size() - 1 );
		}
		
		for(String newLine : newFile){
			newContent.append( newLine ).append( "\n" );
		}
		
		return newContent.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}