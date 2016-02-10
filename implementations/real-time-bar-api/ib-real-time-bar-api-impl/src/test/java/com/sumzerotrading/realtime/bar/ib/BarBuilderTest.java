/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sumzerotrading.realtime.bar.ib;

import com.sumzerotrading.data.BarData;
import com.sumzerotrading.data.BarData.LengthUnit;
import com.sumzerotrading.data.StockTicker;
import com.sumzerotrading.data.Ticker;
import com.sumzerotrading.historicaldata.IHistoricalDataProvider;
import com.sumzerotrading.marketdata.ILevel1Quote;
import com.sumzerotrading.marketdata.QuoteType;
import com.sumzerotrading.realtime.bar.RealtimeBarListener;
import com.sumzerotrading.realtime.bar.RealtimeBarRequest;
import com.sumzerotrading.realtime.bar.ib.util.RealtimeBarUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import junit.framework.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;

/**
 *
 * @author Rob Terpilowski
 */
public class BarBuilderTest extends TestCase {

    protected Mockery mockery;

    public BarBuilderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        mockery = new Mockery();

    }

    @After
    public void tearDown() {
    }

    @Test
    public void testConstructor() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
        final List<BarData> barList = new ArrayList<BarData>();
        double barOpen = 1;
        double barHigh = 2;
        double barLow = 1;
        double barClose = 1;
        int barVolume = 10;
        
        
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,11));
        barList.add( new BarData(LocalDateTime.now(), barOpen, barHigh, barLow, barClose, barVolume) );
        

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));
                
                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                one(mockScheduler).isStarted();
                will(returnValue(true));
            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        assertEquals(request, builder.realtimeBarRequest);
        assertEquals( barOpen, builder.open );
        assertEquals( barHigh, builder.high );
        assertEquals( barLow, builder.low );
        assertEquals( barClose, builder.close );
        assertEquals( barVolume, builder.volume );

    }
    

    @Test
    public void testConstructor_schedulerNotStarted() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
        final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));                

                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                one(mockScheduler).isStarted();
                will(returnValue(false));

                one(mockScheduler).start();
            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        assertEquals(request, builder.realtimeBarRequest);

    }    

    @Test
    public void testConstructor_throwsSchedulerException() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));


        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(throwException(new SchedulerException()));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 2, request.getTimeUnit(), request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));                
            }
        });

        try {
            BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
            fail();
        } catch (IllegalStateException ex) {
            //this should happen
        }
    }

    @Test
    public void testQuoteReceived() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final ILevel1Quote mockQuote = mockery.mock(ILevel1Quote.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));

                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));
                
                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                
                one(mockScheduler).isStarted();
                will(returnValue(true));                

                one(mockQuote).getType();
                will(returnValue(QuoteType.LAST));
                
                one(mockQuote).getType();
                will(returnValue(QuoteType.LAST));                

                one(mockQuote).getValue();
                will(returnValue(BigDecimal.ONE));

//                one(mockQuote).getValue();
//                will(returnValue(BigDecimal.ONE));
//
//                one(mockQuote).getValue();
//                will(returnValue(BigDecimal.ONE));
//
//                one(mockQuote).getValue();
//                will(returnValue(BigDecimal.ONE));
            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        builder.quoteRecieved(mockQuote);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testQuoteReceived_volume() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final ILevel1Quote mockQuote = mockery.mock(ILevel1Quote.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));
                
                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                
                one(mockScheduler).isStarted();
                will(returnValue(true));                

                one(mockQuote).getType();
                will(returnValue(QuoteType.VOLUME));
                
                one(mockQuote).getValue();
                will(returnValue(BigDecimal.ONE));

            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        builder.quoteRecieved(mockQuote);
        mockery.assertIsSatisfied();
    }

    @Test
    public void testAddRemoveBarListeners() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final RealtimeBarListener mockBarListener = mockery.mock(RealtimeBarListener.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));                
                
                one(mockScheduler).isStarted();
                will(returnValue(true));                

                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));

            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        builder.addBarListener(mockBarListener);

        assertEquals(1, builder.listenerList.size());
        assertEquals(mockBarListener, builder.listenerList.get(0));

        builder.removeBarListener(mockBarListener);
        assertEquals(0, builder.listenerList.size());
        mockery.assertIsSatisfied();
    }

    @Test
    public void testSetHigh() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));                

                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                
                one(mockScheduler).isStarted();
                will(returnValue(true));                

            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        assertEquals(3.0, builder.high);
        builder.setHigh(100.0);
        assertEquals(100.0, builder.high);

        builder.setHigh(50.0);
        assertEquals(100.0, builder.high);

        builder.setHigh(200.0);
        assertEquals(200.0, builder.high);


        mockery.assertIsSatisfied();
    }

    @Test
    public void testSetLow() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,200.0,2,10 ));

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));                

                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                
                one(mockScheduler).isStarted();
                will(returnValue(true));                

            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        assertEquals(200.0, builder.low);
        builder.setLow(100.0);
        assertEquals(100.0, builder.low);

        builder.setLow(50.0);
        assertEquals(50.0, builder.low);

        builder.setHigh(200.0);
        assertEquals(50.0, builder.low);


        mockery.assertIsSatisfied();
    }

    @Test
    public void testSetOpen() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));                

                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                
                one(mockScheduler).isStarted();
                will(returnValue(true));                

            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        assertEquals(2.0, builder.open);
        
        builder.openInitialized = false;
        builder.setOpen(100.0);
        assertEquals(100.0, builder.open);

        builder.setOpen(50.0);
        assertEquals(100.0, builder.open);

        mockery.assertIsSatisfied();
    }

    @Test
    public void testSetClose() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));

                                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));

                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                
                one(mockScheduler).isStarted();
                will(returnValue(true));                

            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        assertEquals(2.0, builder.close);
        builder.setClose(100.0);
        assertEquals(100.0, builder.close);

        builder.setClose(50.0);
        assertEquals(50.0, builder.close);

        mockery.assertIsSatisfied();
    }

    @Test
    public void testSetVolume() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(),1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));                

                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                
                one(mockScheduler).isStarted();
                will(returnValue(true));                

            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        assertEquals(10, builder.volume);
        builder.setVolume(100);
        assertEquals(110, builder.volume);

        builder.setVolume(50);
        assertEquals(160, builder.volume);

        mockery.assertIsSatisfied();
    }

    @Test
    public void testBuildBarAndFireEvents() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));
        double open = 1.0;
        double high = 2.0;
        double low = 1.0;
        double close = 2.0;
        int volume = 100;



        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));                

                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                
                one(mockScheduler).isStarted();
                will(returnValue(true));                

            }
        });

        MockFireEventBarBuilder builder = new MockFireEventBarBuilder(mockFactory, request, mockHistoricalDataProvider);
        builder.openInitialized = false;
        builder.highInitialized = false;
        builder.lowInitialized = false;
        builder.volume = 0;
        
        builder.setOpen(open);
        builder.setHigh(high);
        builder.setLow(low);
        builder.setClose(close);
        builder.setVolume(volume);

        LocalDateTime barDate = RealtimeBarUtil.getBarDate();

        builder.buildBarAndFireEvents();
        assertEquals(builder.close, builder.open);
        assertEquals(builder.close, builder.high);
        assertEquals(builder.close, builder.low);
        assertEquals(0, builder.volume);
        assertFalse( builder.highInitialized );
        assertFalse( builder.lowInitialized );
        assertFalse( builder.openInitialized );

        BarData expectedBar = new BarData();
        expectedBar.setOpen(open);
        expectedBar.setHigh(high);
        expectedBar.setLow(low);
        expectedBar.setClose(close);
        expectedBar.setVolume(volume);
        expectedBar.setDateTime(barDate);

        assertEquals(expectedBar, builder.getBar());
        mockery.assertIsSatisfied();
    }

    @Test
    public void testFireEvent() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
        final RealtimeBarListener mockListener = mockery.mock(RealtimeBarListener.class);
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));
        double open = 1.0;
        double high = 2.0;
        double low = 1.0;
        double close = 2.0;
        int volume = 100;


        final BarData expectedBar = new BarData();
        expectedBar.setOpen(open);
        expectedBar.setHigh(high);
        expectedBar.setLow(low);
        expectedBar.setClose(close);
        expectedBar.setVolume(volume);
        expectedBar.setDateTime(LocalDateTime.now());


        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));                

                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                
                one(mockScheduler).isStarted();
                will(returnValue(true));                
                one(mockListener).realtimeBarReceived(request.getRequestId(), request.getTicker(), expectedBar);


            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        builder.addBarListener(mockListener);


        builder.fireEvent(expectedBar);

        mockery.assertIsSatisfied();
    }



    @Test
    public void testGetListenerCount() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
        final RealtimeBarListener mockListener = mockery.mock(RealtimeBarListener.class);
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));                

                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                
                one(mockScheduler).isStarted();
                will(returnValue(true));                


            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);

        assertEquals(0, builder.getListenerCount());
        mockery.assertIsSatisfied();
    }

    @Test
    public void testStop() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
        final JobDetail job = RealtimeBarUtil.buildJob(jobName,null);
        final RealtimeBarListener mockListener = mockery.mock(RealtimeBarListener.class);
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));                

                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));
                
                one(mockScheduler).isStarted();
                will(returnValue(true));                

                one(mockScheduler).deleteJob(job.getKey());
            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        builder.stop();

        mockery.assertIsSatisfied();
    }

    @Test
    public void testStop_ThrowsException() throws Exception {
        final SchedulerFactory mockFactory = mockery.mock(SchedulerFactory.class);
        final Scheduler mockScheduler = mockery.mock(Scheduler.class);
        final IHistoricalDataProvider mockHistoricalDataProvider = mockery.mock( IHistoricalDataProvider.class );
        Ticker ticker = new StockTicker("qqq");
        final RealtimeBarRequest request = new RealtimeBarRequest(1, ticker, 1, LengthUnit.MINUTE);
        String jobName = RealtimeBarUtil.getJobName(request);
        final Trigger trigger = RealtimeBarUtil.getTrigger(jobName, request.getTimeInteval(), request.getTimeUnit());
        final JobDetail job = RealtimeBarUtil.buildJob(jobName,null);
        final RealtimeBarListener mockListener = mockery.mock(RealtimeBarListener.class);
final List<BarData> barList = new ArrayList<BarData>();
        barList.add( new BarData(LocalDateTime.now(), 2,3,2,2,10 ));

        mockery.checking(new Expectations() {

            {
                one(mockFactory).getScheduler();
                will(returnValue(mockScheduler));
                
                one(mockHistoricalDataProvider).requestHistoricalData(request.getTicker(), 1, LengthUnit.DAY, request.getTimeInteval(), request.getTimeUnit(), IHistoricalDataProvider.ShowProperty.TRADES, false);
                will(returnValue(barList));                

                one(mockScheduler).scheduleJob(with(any(JobDetail.class)), with(any(Trigger.class)));

                one(mockScheduler).isStarted();
                will(returnValue(true));
                
                one(mockScheduler).deleteJob(job.getKey());
                will(throwException(new SchedulerException()));
            }
        });

        BarBuilder builder = new BarBuilder(mockFactory, request, mockHistoricalDataProvider);
        try {
            builder.stop();
            fail();
        } catch (IllegalStateException ex) {
            //this should happen
        }

        mockery.assertIsSatisfied();
    }


    private static class MockFireEventBarBuilder extends BarBuilder {

        private BarData bar = null;

        public MockFireEventBarBuilder(SchedulerFactory schedulerFactory, RealtimeBarRequest request, IHistoricalDataProvider dataProvider ) {
            super(schedulerFactory, request, dataProvider);
        }

        @Override
        protected void fireEvent(BarData bar) {
            this.bar = bar;
        }

        public BarData getBar() {
            return bar;
        }
    }
}
