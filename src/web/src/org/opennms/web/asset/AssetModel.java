package org.opennms.web.asset;

import java.sql.*;
import java.text.ParseException;
import java.util.Date;
import java.util.Vector;
import org.opennms.core.resource.Vault;

public class AssetModel extends Object
{

    public Asset getAsset( int nodeId ) throws SQLException {
        Asset asset = null;
        Connection conn = Vault.getDbConnection();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM ASSETS WHERE NODEID=" + nodeId );

            Asset[] assets = rs2Assets( rs );

            //what if this returns more than one?
            if( assets.length > 0 ) {
                asset = assets[0];
            }

            rs.close();
            stmt.close();
        }
        finally {
            Vault.releaseDbConnection( conn );
        }

        return( asset );
    }


    public Asset[] getAllAssets() throws SQLException {
        Asset[] assets = new Asset[0];
        Connection conn = Vault.getDbConnection();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM ASSETS" );

            assets = rs2Assets( rs );

            rs.close();
            stmt.close();
        }
        finally {
            Vault.releaseDbConnection( conn );
        }

        return( assets );
    }


    public void createAsset( Asset asset ) throws SQLException {
        if( asset == null ) {
            throw new IllegalArgumentException( "Cannot take null parameters." );
        }

        Connection conn = Vault.getDbConnection();

        try {
            PreparedStatement stmt = conn.prepareStatement( "INSERT INTO ASSETS (nodeID,category,manufacturer,vendor,modelNumber,serialNumber,description,circuitId,assetNumber,operatingSystem,rack,slot,port,region,division,department,address1,address2,city,state,zip,building,floor,room,vendorPhone,vendorFax,userLastModified,lastModifiedDate,dateInstalled,lease,leaseExpires,supportPhone,maintContract,vendorAssetNumber,maintContractExpires,comment) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            stmt.setInt( 1, asset.nodeId );
            stmt.setString( 2, asset.category );
            stmt.setString( 3, asset.manufacturer );
            stmt.setString( 4, asset.vendor );
            stmt.setString( 5, asset.modelNumber );
            stmt.setString( 6, asset.serialNumber );
            stmt.setString( 7, asset.description );
            stmt.setString( 8, asset.circuitId );
            stmt.setString( 9, asset.assetNumber );
            stmt.setString( 10, asset.operatingSystem );
            stmt.setString( 11, asset.rack );
            stmt.setString( 12, asset.slot );
            stmt.setString( 13, asset.port );
            stmt.setString( 14, asset.region );
            stmt.setString( 15, asset.division );
            stmt.setString( 16, asset.department );
            stmt.setString( 17, asset.address1 );
            stmt.setString( 18, asset.address2 );
            stmt.setString( 19, asset.city );
            stmt.setString( 20, asset.state );
            stmt.setString( 21, asset.zip );
            stmt.setString( 22, asset.building );
            stmt.setString( 23, asset.floor );
            stmt.setString( 24, asset.room );
            stmt.setString( 25, asset.vendorPhone );
            stmt.setString( 26, asset.vendorFax );
            stmt.setString( 27, asset.userLastModified );
            stmt.setTimestamp( 28, new Timestamp(asset.lastModifiedDate.getTime()));
            stmt.setString( 29, asset.dateInstalled);
            stmt.setString( 30, asset.lease );
            stmt.setString( 31, asset.leaseExpires);
            stmt.setString( 32, asset.supportPhone );
            stmt.setString( 33, asset.maintContract );
            stmt.setString( 34, asset.vendorAssetNumber );
            stmt.setString( 35, asset.maintContractExpires );
            stmt.setString( 36, asset.comments );
            
            stmt.execute();
            stmt.close();
        }
        finally {
            Vault.releaseDbConnection( conn );
        }
    }


    public void modifyAsset( Asset asset ) throws SQLException {
        if( asset == null ) {
            throw new IllegalArgumentException( "Cannot take null parameters." );
        }

        Connection conn = Vault.getDbConnection();

        try {
            PreparedStatement stmt = conn.prepareStatement( "UPDATE ASSETS SET category=?,manufacturer=?,vendor=?,modelNumber=?,serialNumber=?,description=?,circuitId=?,assetNumber=?,operatingSystem=?,rack=?,slot=?,port=?,region=?,division=?,department=?,address1=?,address2=?,city=?,state=?,zip=?,building=?,floor=?,room=?,vendorPhone=?,vendorFax=?,userLastModified=?,lastModifiedDate=?,dateInstalled=?,lease=?,leaseExpires=?,supportPhone=?,maintContract=?,vendorAssetNumber=?,maintContractExpires=?,comment=? WHERE nodeid=?");
            stmt.setString( 1, asset.category );
            stmt.setString( 2, asset.manufacturer );
            stmt.setString( 3, asset.vendor );
            stmt.setString( 4, asset.modelNumber );
            stmt.setString( 5, asset.serialNumber );
            stmt.setString( 6, asset.description );
            stmt.setString( 7, asset.circuitId );
            stmt.setString( 8, asset.assetNumber );
            stmt.setString( 9, asset.operatingSystem );
            stmt.setString( 10, asset.rack );
            stmt.setString( 11, asset.slot );
            stmt.setString( 12, asset.port );
            stmt.setString( 13, asset.region );
            stmt.setString( 14, asset.division );
            stmt.setString( 15, asset.department );
            stmt.setString( 16, asset.address1 );
            stmt.setString( 17, asset.address2 );
            stmt.setString( 18, asset.city );
            stmt.setString( 19, asset.state );
            stmt.setString( 20, asset.zip );
            stmt.setString( 21, asset.building );
            stmt.setString( 22, asset.floor );
            stmt.setString( 23, asset.room );
            stmt.setString( 24, asset.vendorPhone );
            stmt.setString( 25, asset.vendorFax );
            stmt.setString( 26, asset.userLastModified );
            stmt.setTimestamp( 27, new Timestamp(asset.lastModifiedDate.getTime()));
            stmt.setString( 28, asset.dateInstalled);
            stmt.setString( 29, asset.lease );
            stmt.setString( 30, asset.leaseExpires);
            stmt.setString( 31, asset.supportPhone );
            stmt.setString( 32, asset.maintContract );
            stmt.setString( 33, asset.vendorAssetNumber );
            stmt.setString( 34, asset.maintContractExpires );
            stmt.setString( 35, asset.comments );
            stmt.setInt( 36, asset.nodeId );
            
            stmt.execute();
            stmt.close();
        }
        finally {
            Vault.releaseDbConnection( conn );
        }
    }


    public static class MatchingAsset extends Object {
        public int nodeId;
        public String nodeLabel;
        public String matchingValue;
        public String columnSearched;
    }


    public static MatchingAsset[] searchAssets( String columnName, String searchText ) throws SQLException {
        if( columnName == null || searchText == null ) {
            throw new IllegalArgumentException( "Cannot take null parameters." );
        }

        MatchingAsset[] assets = new MatchingAsset[0];
        Connection conn = Vault.getDbConnection();
        Vector vector = new Vector();

        try {
            PreparedStatement stmt = conn.prepareStatement( "SELECT NODEID, NODE.NODELABEL, " + columnName + " FROM ASSETS WHERE LOWER(" + columnName + ") LIKE ? AND ASSETS.NODEID=NODE.NODEID" );
            stmt.setString( 1, "%" + searchText.toLowerCase() + "%" );
            
            ResultSet rs = stmt.executeQuery();

            while( rs.next() ) {
                MatchingAsset asset = new MatchingAsset();

                Object element = new Integer(rs.getInt("nodeID"));
                asset.nodeId = ((Integer)element).intValue();
                asset.nodeLabel = rs.getString( "nodelabel" );  
                asset.matchingValue = rs.getString( columnName );
                asset.columnSearched = columnName;

                vector.addElement( asset );
            }

            rs.close();
            stmt.close();
        }
        finally {
            Vault.releaseDbConnection( conn );
        }

        assets = new MatchingAsset[vector.size()];

        for( int i=0; i < assets.length; i++ ) {
            assets[i] = (MatchingAsset)vector.elementAt(i);
        }
        
        return( assets );
    }

    protected static Asset[] rs2Assets( ResultSet rs ) throws SQLException {
        Asset[] assets = null;
        Vector vector = new Vector();

        while(rs.next()) {
            Asset asset = new Asset();
        
            Object element = new Integer(rs.getInt("nodeID"));
            asset.nodeId = ((Integer)element).intValue();

            asset.setCategory( rs.getString("category"));
            asset.setManufacturer(rs.getString("manufacturer"));
            asset.setVendor(rs.getString("vendor"));
            asset.setModelNumber(rs.getString("modelNumber"));
            asset.setSerialNumber (rs.getString("serialNumber"));
            asset.setDescription (rs.getString("description"));
            asset.setCircuitId (rs.getString("circuitId"));
            asset.setAssetNumber (rs.getString("assetNumber"));
            asset.setOperatingSystem (rs.getString("operatingSystem"));
            asset.setRack(rs.getString("rack"));
            asset.setSlot(rs.getString("slot"));
            asset.setPort( rs.getString("port"));
            asset.setRegion ( rs.getString("region"));
            asset.setDivision ( rs.getString("division"));
            asset.setDepartment ( rs.getString("department"));
            asset.setAddress1 ( rs.getString("address1"));
            asset.setAddress2 ( rs.getString("address2"));
            asset.setCity ( rs.getString("city"));
            asset.setState ( rs.getString("state"));
            asset.setZip ( rs.getString("zip"));
            asset.setBuilding ( rs.getString("building"));
            asset.setFloor ( rs.getString("floor"));
            asset.setRoom ( rs.getString("room"));
            asset.setVendorPhone ( rs.getString("vendorPhone"));
            asset.setVendorFax ( rs.getString("vendorFax"));
            asset.setUserLastModified ( rs.getString("userLastModified"));
            asset.setLease ( rs.getString("lease"));
            asset.setSupportPhone ( rs.getString("supportPhone"));
            asset.setMaintContract ( rs.getString("maintContract"));
            asset.setDateInstalled ( rs.getString("dateInstalled"));
            asset.setLeaseExpires ( rs.getString("leaseExpires"));
            asset.setMaintContractExpires ( rs.getString("maintContractExpires"));
            asset.setVendorAssetNumber ( rs.getString("vendorAssetNumber"));
            asset.setComments ( rs.getString("comment"));

            element = rs.getTimestamp("lastModifiedDate");
            asset.lastModifiedDate = new java.util.Date(((Timestamp)element).getTime());

            vector.addElement( asset );
        }

        assets = new Asset[vector.size()];

        for( int i=0; i < assets.length; i++ ) {
            assets[i] = (Asset)vector.elementAt(i);
        }

        return assets;
    }

    
    
    public String[][] getColumns() {
        return( this.columns );
    }
    
    
    /** 
     * Hard-coded (for now) list of human-readable asset columns and the
     * corresponding database column. 
     */
    protected String[][] columns = new String[][] {
        new String[] { "Address 1", "address1" },
        new String[] { "Address 2", "address2" },
        new String[] { "Asset Number", "assetNumber" },
        new String[] { "Building", "building" },
        new String[] { "Circuit ID", "circuitId" },
        new String[] { "City", "city" },
        new String[] { "Comments", "comment" },
        new String[] { "Date Installed", "dateInstalled" },
        new String[] { "Department", "department" },
        new String[] { "Description", "description" },
        new String[] { "Division", "division" },
        new String[] { "Floor", "floor" },
        new String[] { "Lease", "lease" },
        new String[] { "Lease Expires", "leaseExpires" },
        new String[] { "Maint Contract", "maintContract" },
        new String[] { "Maint Contract Expires", "maintContractExpires" },
        new String[] { "Maint Phone", "supportPhone" },
        new String[] { "Manufacturer", "manufacturer" },
        new String[] { "Model Number", "modelNumber" },
        new String[] { "Operating System", "operatingSystem" },
        new String[] { "Port", "port" },
        new String[] { "Rack", "rack" },
        new String[] { "Region", "region" },
        new String[] { "Room", "room" },
        new String[] { "Serial Number", "serialNumber" },
        new String[] { "Slot", "slot" },
        new String[] { "State", "state" },
        new String[] { "User Last Modified", "userLastModified" },
        new String[] { "Vendor", "vendor" },
        new String[] { "Vendor Asset Number", "vendorAssetNumber" },
        new String[] { "Vendor Fax", "vendorFax" },
        new String[] { "Vendor Phone", "vendorPhone" },
        new String[] { "ZIP Code", "zip" }
    };    
}


