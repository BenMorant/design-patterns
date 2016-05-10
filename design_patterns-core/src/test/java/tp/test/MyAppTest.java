package tp.test;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tp.dao.ProduitDao;
import tp.dao.ProduitDaoFactory;
import tp.entity.Produit;

public class MyAppTest {
	
	private static Logger logger = LoggerFactory.getLogger(MyAppTest.class);
	
	private ProduitDao produitDao; //chose à  tester

    //pour eventuel lancement "run as / java appli"
	public static void main(String[] args) {
		MyAppTest thisApp = new MyAppTest();
		thisApp.setUp();
		thisApp.testSingleton();
		thisApp.testProduitDao();
		thisApp.clean();
		//System.exit(0);
	}
	
	@Before
	public void setUp(){
		ProduitDaoFactory produitDaoFactory = ProduitDaoFactory.getInstance();
        this.produitDao = produitDaoFactory.createProduitDao();
	}
	
	@After
	public void clean(){
		this.produitDao.cleanUpResources();
	}
	
	//@Test
	public void listerTousLesProduits()
	{
		List<Produit> listProd = produitDao.getAllProduits();
		Assert.assertNotNull(listProd);
		logger.info("liste des produits ==>");
		for(Produit p : listProd)
			logger.info(p.toString());
	}
	
	private void subTestSingleton(){
		ProduitDaoFactory produitDaoFactory = ProduitDaoFactory.getInstance();
		String data = produitDaoFactory.getCommonData();
		Assert.assertTrue(data.equals("my shared data inside singleton"));
	}
	
	@Test
	public void testSingleton(){
		//ProduitDaoFactory prodDaoFactory = new ProduitDaoFactory(); //impossible si constructeur privé
		ProduitDaoFactory prodDaoFactory = ProduitDaoFactory.getInstance();
		prodDaoFactory.setCommonData("my shared data inside singleton");
		subTestSingleton();
	}
	
	@Test
	public void testProduitDao()
	{
        
        listerTousLesProduits();
        
        Produit nouveauProduit = new Produit(null,"nouveau produit XY",26.6);
        nouveauProduit = produitDao.createProduit(nouveauProduit);
        Long ref = nouveauProduit.getReference();
        Assert.assertNotNull(ref);
        Produit prod = produitDao.getProduitByRef(ref);
        Assert.assertTrue(prod.getLabel().equals("nouveau produit XY"));
        Assert.assertEquals(26.6, prod.getPrix(), 0.00001);
        logger.info("nouveau produit (with ref="+ ref + "):");
        logger.info(prod.toString());
        
        prod.setLabel("nouveau label");
        prod.setPrix(65.2);
        produitDao.updateProduit(prod);
        
        prod = produitDao.getProduitByRef(ref);
        Assert.assertTrue(prod.getLabel().equals("nouveau label"));
        Assert.assertEquals(65.2, prod.getPrix(), 0.00001);
        logger.info("nouveau produit modifie:");
        logger.info(prod.toString());
       
        produitDao.deleteProduit(prod);
        Produit deletedProd = produitDao.getProduitByRef(ref);
        if(deletedProd==null)
        	 logger.info("==>nouveau produit finalement supprime.");
        Assert.assertNull(deletedProd);
        
	}

}
